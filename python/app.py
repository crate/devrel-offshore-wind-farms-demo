from dotenv import load_dotenv
from flask import Flask, abort, render_template
from crate import client

# Load environment variables / secrets from .env file.
load_dotenv()

app = Flask(__name__)

# Connect to CrateDB.
# TODO make this configurable.
# TODO catch error and shutdown.
conn = client.connect("http://localhost:4200", username="crate", password="", verify_ssl_cert=False)

@app.route("/api/windfarms")
def get_windfarms():
    results = { "results": [] }

    cursor = conn.cursor()

    try:
        cursor.execute("SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC")
    
        if cursor.rowcount == 0:
            abort(404, "No windfarm data found.")

        # Desired return value shape:
        # { results: [ array of objects for each wind farm] }
        for windfarm in cursor.fetchall():
            result = {
                "id": windfarm[0],
                "name": windfarm[1],
                "description": windfarm[2],
                "location": {
                    "x": windfarm[3][0],
                    "y": windfarm[3][1]
                },
                "boundaries": windfarm[4],
                "turbines": windfarm[5]
            }

            results['results'].append(result)

    finally:
        cursor.close()
    
    return results

@app.route("/api/latest/<string:id>")
def get_latest_for_windfarm(id):
    return "TODO"

@app.route("/api/avgpctformonth/<string:id>/<int:ts>")
def get_avg_windfarm_pct_for_month(id, ts):
    return "TODO"

@app.route("/api/outputforday/<string:id>/<int:ts>")
def get_windfarm_output_for_day(id, ts):
    return "TODO"

@app.route("/api/dailymaxpct/{string:id}/{int:days}")
def get_windfarm_max_pct_for_day(id, days):
    return "TODO"

@app.route("/")
def homepage():
    return render_template("index.html")

if __name__ == "__main__":
    # TODO configure Flask port.
    app.run(port=8000)