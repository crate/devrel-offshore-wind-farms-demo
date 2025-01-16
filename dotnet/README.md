# CrateDB Offshore Wind Farms Demo: C#

## Introduction

Follow the instructions below to configure and run the C# implementation of this project.  You'll need to have completed the instructions in the [main README](../README.md) first and should have created a CrateDB database and loaded the example data into it.

## Configuring the Database Connection

You'll need to configure the project to talk to your CrateDB database.  How you do this depends on whether you chose the cloud or local option...

First, in your terminal, change directory to `dotnet/OffshoreWindFarmsDemo`:

```bash
cd dotnet/OffshoreWindFarmsDemo
```

### Cloud Option

Use your text editor / IDE to open the file `appsettings.json`.

The file's contents should look like this:

```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "ConnectionStrings": {
    "CrateDB": "Host=127.0.0.1;Username=crate;Password=;Database=doc"
  },
  "AllowedHosts": "*"
}
```

Edit the value of the key `CrateDB` in the `ConnectionStrings` object. Make the following changes:

* Replace `Host=127.0.0.1` with the hostname of your cloud database (example: `Host=my-cluster.gke1.us-central1.gcp.cratedb.net`).
* Replace `Username=crate` with `Username=admin`.
* Replace `Password=` with the password for yuour cloud database (example `Password=sdfW234fwfTY^f`).

After making your changes, your JSON should looke like this:

```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "ConnectionStrings": {
    "CrateDB": "Host=my-cluster.gke1.us-central1.gcp.cratedb.net;Username=admin;Password=sdfW234fwfTY^f;Database=doc"
  },
  "AllowedHosts": "*"
}
```

Save your changes.

### Local Option

The project comes pre-configured to expect CrateDB to be at `127.0.0.1:5432` so there's nothing to do here (unless you changed the port in the Docker Compose file). Our Docker Compose file exports port 5432 from the container, so simply carry on to the next step :)

If you changed the port in the Docker Compose file (for example because you are also running Postgres locally on port 5432), then you'll need to edit `appsettings.Development.json` and change `Host=127.0.0.1` to reflect your updated port number (example: `Host=127.0.0.1:6666`).  Be sure to save your changes before continuing.

## Running the Project

There are two ways to start the application.  If you are planning to modify the source code and want the server to live reload when you save a source file, use this:

```bash
dotnet watch
```

If you just want to run the server and aren't planning to edit the source code, start it like this:

```bash
dotnet run
```

Once you have the server running, point your browser at port 5213 and you should see the map front end:

```
http://localhost:5213/
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

`http://localhost:5213/api/windfarms`

* Clicking on a wind farm marker on the map loads additional data for that wind farm, using the wind farm's ID.  Here's an example for North Hoyle (`NHOYW-1`): 

`http://localhost:5213/api/latest/NHOYW-1`

* When you click on a wind farm marker, the average output percentage for the month is returned from this endpoint.  The parameters are the wind farm ID (`NHOY-1` here) and the timestamp for the 1st of the month (`1727740800000` here).  Example: 

`http://localhost:5213/api/avgpctformonth/NHOYW-1/1727740800000`

* Cumulative output for the most recent day in the dataset is also displayed when you click on a wind farm marker. Parameters for this endpoint are also the wind farm ID (`NHOY-1` here) and the timestamp for midnight for the day you want data for (`1730073600000` here). Example: 

`http://localhost:5213/api/outputforday/NHOYW-1/1730073600000`

* Clicking on the polygon for a wind farm loads further data for that wind farm, showing the maximum output percentage for a number of days.  Here's an example for Teeside (`TEES-1`) for 10 days:

`http://localhost:5213/api/dailymaxpct/TEES-1/10`

## Shutting Down

To stop the application, press `Ctrl-C` in the terminal window that you started it from.

If you're using Docker to run CrateDB, stop the container like so:

```bash
docker compose down
```