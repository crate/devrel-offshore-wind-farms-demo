# CrateDB C# Offshore Wind Farms Demo Application

```bash
cd OffshoreWindFarmsDemo
dotnet run
```

```bash
dotnet watch
```

```
http://localhost:5213/
```

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

```sql
COPY windfarms                                 
FROM 'https://github.com/crate/cratedb-datasets/raw/main/devrel/uk-offshore-wind-farm-data/wind_farms.json'
RETURN SUMMARY;
```

(45 records)

```sql
CREATE TABLE windfarm_output (
   windfarmid TEXT,
   ts TIMESTAMP WITHOUT TIME ZONE,
   day TIMESTAMP WITH TIME ZONE GENERATED ALWAYS AS date_trunc('day', ts),
   output DOUBLE PRECISION,
   outputpercentage DOUBLE PRECISION
) PARTITIONED BY (day);
```

```sql
COPY windfarm_output
FROM 'https://github.com/crate/cratedb-datasets/raw/main/devrel/uk-offshore-wind-farm-data/wind_farm_output.json.gz' 
WITH (compression='gzip')
RETURN SUMMARY;
```

(75825 records)