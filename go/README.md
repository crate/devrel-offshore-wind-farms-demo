# CrateDB Offshore Wind Farms Demo: Go

## Introduction

Follow the instructions below to configure and run the Go implementation of this project.  You'll need to have completed the instructions in the [main README](../README.md) first and should have created a CrateDB database and loaded the example data into it.

## Configuring the Database Connection

You'll need to configure the project to talk to your CrateDB database.  

The first step is to create a `.env` file in which to store your database credentials:

```bash
cd go
cp env.example .env
```

The next step depends on whether you chose the cloud or local option...

### Cloud Option

Use your text editor / IDE to open the file `.env`.

The file's contents should look like this:

```bash
CRATEDB_URL=postgres://crate:@localhost:5432/doc
PORT=8000
```

Edit the value of the key `CRATEDB_URL` to include your cloud database credentials. The format is:

```
postgres://username:password@hostname:5432/doc
```

For example, if your host name is `my-cluster.gke1.us-central1.gcp.cratedb.net`, username is `admin` and password is `sdfW234fwfTY^f` then your URL should look like this:

```
postgres://admin:sdfW234fwfTY^f@my-cluster.gke1.us-central1.gcp.cratedb.net:5432/doc
```

Save your changes.

### Local Option

The project comes pre-configured to expect CrateDB to be running with the default configuration on localhost, so there's nothing to do here.

## Running the Project

Start the application like this:

```bash
go run server.go
```

Once you have the server running, point your browser at port 8000 and you should see the map front end:

```
http://localhost:8000/
```

## Interacting with the Project

When the project is first loaded, it displays a map of the UK with a blue marker for each wind farm.  Click on one of these markers to show a pop-up containing details about that wind farm's latest and monthly average outputs, as well as a table with the running total of the output for each hour of the most recent day in the dataset.

![The user has clicked on the marker for Rampion wind farm](../wind_farm_marker_clicked.png)

Next, zoom in a bit until the wind farm markers are replaced with polygons showing the boundaries of each wind farm.  Click on one of the polygons to see a marker containing data about the maximum output of the wind farm for the 10 most recent days in the dataset.

![The user has clicked on the polygon for Triton Knoll wind farm](../wind_farm_polygon_clicked.png)

Finally, zoom in some more to see the locations of individual turbines in the wind farms.  These markers are not clickable.

![Zoomed in further to show the turbine locations of several wind farms](../wind_farm_turbines.png)

## Try out the API Calls

You can see the raw that that the front end uses by visiting the API URLs whilst the application is running:

* When the page initially loads, it calls this endpoint to get data about all of the wind farms:

`http://localhost:8000/api/windfarms`

* Clicking on a wind farm marker on the map loads additional data for that wind farm, using the wind farm's ID.  Here's an example for North Hoyle (`NHOYW-1`): 

`http://localhost:8000/api/latest/NHOYW-1`

* When you click on a wind farm marker, the average output percentage for the month is returned from this endpoint.  The parameters are the wind farm ID (`NHOY-1` here) and the timestamp for the 1st of the month (`1727740800000` here).  Example: 

`http://localhost:8000/api/avgpctformonth/NHOYW-1/1727740800000`

* Cumulative output for the most recent day in the dataset is also displayed when you click on a wind farm marker. Parameters for this endpoint are also the wind farm ID (`NHOY-1` here) and the timestamp for midnight for the day you want data for (`1730073600000` here). Example: 

`http://localhost:8000/api/outputforday/NHOYW-1/1730073600000`

* Clicking on the polygon for a wind farm loads further data for that wind farm, showing the maximum output percentage for a number of days.  Here's an example for Teeside (`TEES-1`) for 10 days:

`http://localhost:8000/api/dailymaxpct/TEES-1/10`

## Shutting Down

To stop the application, press `Ctrl-C` in the terminal window that you started it from.

If you're using Docker to run CrateDB, stop the container like so:

```bash
docker compose down
```