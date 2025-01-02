# CrateDB C# Offshore Wind Farms Demo Application

## Introduction

TODO

## Prerequisites

To run this project you'll need to install the following software:

* .NET SDK ([download](https://dotnet.microsoft.com/en-us/download)) - we've tested this project with version 9.0 on macOS Sequoia.
* Git command line tools ([download](https://git-scm.com/downloads)).
* Your favorite code editor, to edit configuration files and browse/edit the code if you wish.  [Visual Studio Code](https://code.visualstudio.com/) is great for this.
* Access to a CrateDB cluster (see below for details).

## Getting a CrateDB Database

You'll need a CrateDB database to store the project's data in.  Choose between a free hosted instance in the cloud, or run the database locally.  Either option is fine.

### Cloud Option

Create a database in the cloud by first pointing your browser at [`console.cratedb.cloud`](https://console.cratedb.cloud/).

Login or create an account, then follow the prompts to create a "CRFREE" database on shared infrastructure in the cloud of your choice (choose from Amazon AWS, Microsoft Azure and Google Cloud).  Pick a region close to where you live to minimize latency between your machine running the code and the database that stores the data. 

Once you've created your cluster, you'll see a "Download" button.  This downloads a text file containing a copy of your database hostname, port, username and password.  Make sure to download these as you'll need them later and won't see them again.

Wait until the cluster status shows a green status icon and "Healthy" status before continuing.  Note that it may take a few moments to provision your database.

### Local Option

The best way to run CrateDB locally is by using Docker.  We've provided a Docker Compose file for you.  Once you've installed [Docker Desktop](https://www.docker.com/products/docker-desktop/), you can start the database like this:

```bash
docker compose up
```

Once the database is up and running, you can access the console by pointing your browser at:

```
http://localhost:4200
```

## Creating the Database Tables

Now you have a database, you'll need to create the tables that this project uses.  Copy and paste the following SQL command into the database console, then execute it to create a table named `windfarms`:

```sql
CREATE TABLE windfarms (
    id TEXT PRIMARY KEY,
    name TEXT,
    description TEXT INDEX USING fulltext WITH (analyzer='english'),
    description_vec FLOAT_VECTOR(2048),
    location GEO_POINT,
    territory TEXT,
    boundaries GEO_SHAPE INDEX USING geohash WITH (PRECISION='1m', DISTANCE_ERROR_PCT=0.025),
    turbines OBJECT(STRICT) AS (
        brand TEXT,
        model TEXT,
        locations ARRAY(GEO_POINT),
        howmany SMALLINT
    ),
    capacity DOUBLE PRECISION,
    url TEXT
);
```

Then copy and paste this statement into the console, and execute it to create a table named `windfarm_output`:

```sql
CREATE TABLE windfarm_output (
   windfarmid TEXT,
   ts TIMESTAMP WITHOUT TIME ZONE,
   day TIMESTAMP WITH TIME ZONE GENERATED ALWAYS AS date_trunc('day', ts),
   output DOUBLE PRECISION,
   outputpercentage DOUBLE PRECISION
) PARTITIONED BY (day);
```

## Populating the Tables with Sample Data

Right now your database tables are empty.  Let's add some sample data!  Copy and paste the following SQL statement into the console then execute it to insert records for each windfarm into the `windfarms` table:

```sql
COPY windfarms                                 
FROM 'https://github.com/crate/cratedb-datasets/raw/main/devrel/uk-offshore-wind-farm-data/wind_farms.json'
RETURN SUMMARY;
```

Examine the output of this command once it's completed.  You should see that 45 records were loaded with 0 errors.

Next, let's load the sample power generation data into the `windfarm_output` table.  Copy and paste this SQL statement into the console, then execute it:

```sql
COPY windfarm_output
FROM 'https://github.com/crate/cratedb-datasets/raw/main/devrel/uk-offshore-wind-farm-data/wind_farm_output.json.gz' 
WITH (compression='gzip')
RETURN SUMMARY;
```

Examine the output of this command once it's completed.  You should expect 75,825 records to have loaded with 0 errors.

## Getting the Code

Next you'll need to get a copy of the code from GitHub by cloning the repository.  Open up your terminal and change directory to wherever you store coding projects, then enter the following commands:

```bash
git clone https://github.com/crate/devrel-offshore-wind-farms-demo.git
```

This creates a new folder named `devrel-offshore-wind-farms-demo`.  You'll need to change directory as follows before running the project.

```bash
cd devrel-offshore-wind-farms-demo/OffshoreWindFarmsDemo
```

## Configuring the Database Connection

You'll need to configure the project to talk to your CrateDB database.  How you do this depends on whether you chose the cloud or local option...

### Cloud Option

TODO - which file is it?

### Local Option

The project comes pre-configured to expect CrateDB to be at `localhost:5432` so there's nothing to do here. Simply carry on to the next step :)

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
