from dotenv import load_dotenv
from flask import Flask, abort, render_template
from crate import client
import os

# Load environment variables / secrets from .env file.
load_dotenv()

app = Flask(__name__)

# Connect to CrateDB.
conn = client.connect(os.environ["CRATEDB_URL"])

@app.route("/api/windfarms")
def get_windfarms():
    results = { "results": [] }

    cursor = conn.cursor()

    try:
        cursor.execute("SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC")
    
        if cursor.rowcount == 0:
            abort(404, "No windfarm data found.")

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
    results = { "results": [] }

    cursor = conn.cursor()

    try:
        cursor.execute(
            "SELECT ts, day, month, output, outputpercentage FROM windfarm_output WHERE windfarmid = ? ORDER BY ts DESC LIMIT 1", 
            (id,)
        )

        if cursor.rowcount == 0:
            abort(404, f"No such windfarm ID: {id}.")

        result = cursor.fetchone()            

        results["results"].append({
            "timestamp": result[0],
            "day": result[1],
            "month": result[2],
            "output": result[3],
            "outputPercentage": result[4] 
        })

    finally:
        cursor.close()
        
    return results

@app.route("/api/avgpctformonth/<string:id>/<int:ts>")
def get_avg_windfarm_pct_for_month(id, ts):
    results = { "results": [] }

    cursor = conn.cursor()

    try:
        cursor.execute(
            "SELECT trunc(avg(outputpercentage), 2) FROM windfarm_output WHERE windfarmid = ? and month = ?",
            (id, ts)
        )

        result = cursor.fetchone()

        if result[0] is None:
            abort(404, f"No such windfarm ID: {id}.")

        results["results"].append({
            "avgPct": result[0]
        })

    finally:
        cursor.close()
        
    return results

@app.route("/api/outputforday/<string:id>/<int:ts>")
def get_windfarm_output_for_day(id, ts):
    results = { "results": [] }

    cursor = conn.cursor()

    try:
        cursor.execute(
            "SELECT extract(hour from ts) as hour, output, sum(output) OVER (ORDER BY ts ASC) FROM windfarm_output WHERE windfarmid = ? AND day = ? ORDER BY hour ASC",
            (id, ts)
        )

        if cursor.rowcount == 0:
            abort(404, f"No data for windfarm ID: {id}.")

        rows = cursor.fetchall()

        for row in rows:
            results["results"].append({
                "hour": row[0],
                "output": row[1],
                "cumulativeOutput": round(row[2], 2)
            })

    finally:
        cursor.close()
        
    return results

@app.route("/api/dailymaxpct/<string:id>/<int:days>")
def get_windfarm_max_pct_for_day(id, days):
    results = { "results": [] }

    cursor = conn.cursor()

    try:
        cursor.execute(
            "SELECT day, max(outputpercentage) FROM windfarm_output WHERE windfarmid = ? GROUP BY day ORDER BY day DESC LIMIT ?",
            (id, days)
        )

        if cursor.rowcount == 0:
            abort(404, f"No data for windfarm ID: {id}.")

        rows = cursor.fetchall()

        for row in rows:
            results["results"].append({
                "day": row[0],
                "maxOutputPercentage": row[1]
            })

    finally:
        cursor.close()
        
    return results

@app.route("/")
def homepage():
    return render_template("index.html")

if __name__ == "__main__":
    app.run(port=os.environ["PORT"])