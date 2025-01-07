using System.Text.Json;
using Npgsql;
using NpgsqlTypes;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllersWithViews();

var app = builder.Build();
app.UseFileServer();

// TODO consider trapping a null here.
await using var dataSource = NpgsqlDataSource.Create(app.Configuration.GetConnectionString("CrateDB")!);

app.MapGet("/api/windfarms", async () => {
    await using var command = dataSource.CreateCommand("SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC");
    await using var reader = await command.ExecuteReaderAsync();

    // TODO can the database driver help with this?
    var windFarms = new List<Object>();

    while (await reader.ReadAsync()) {
        windFarms.Add(
            new {
                id = reader.GetString(0),
                name = reader.GetString(1),
                description = reader.GetString(2),
                location = reader.GetFieldValue<NpgsqlPoint>(3),
                boundaries = reader.GetFieldValue<JsonDocument>(4),
                turbines = reader.GetFieldValue<JsonDocument>(5)
            }
        );
    }

    return Results.Ok(new {
        results = windFarms
    });
});

app.MapGet("/api/latest/{id}", async (string id) => {
    await using var conn = await dataSource.OpenConnectionAsync();

    await using var command = new NpgsqlCommand(
        "SELECT ts, day, month, output, outputpercentage FROM windfarm_output WHERE windfarmid = $1 ORDER BY ts DESC LIMIT 1",
        conn
    ) {
        Parameters = 
        {
            new() { Value = id }
        }
    };
        
    await using var reader = await command.ExecuteReaderAsync();
    await reader.ReadAsync();

    if (reader.HasRows) {
        var data = new List<Object>();
        data.Add(new {
            timestamp = ((DateTimeOffset)reader.GetDateTime(0)).ToUnixTimeMilliseconds(),
            day = ((DateTimeOffset)reader.GetDateTime(1)).ToUnixTimeMilliseconds(),
            month = ((DateTimeOffset)reader.GetDateTime(2)).ToUnixTimeMilliseconds(),
            output = reader.GetDouble(3),
            outputPercentage = reader.GetDouble(4) 
        });

        return Results.Ok(new {
            results = data
        });
    } else {
        return Results.NotFound($"No such windfarm ID: {id}");
    }
});

app.MapGet("/api/avgpctformonth/{id}/{ts}", async (string id, long ts) => {
    await using var conn = await dataSource.OpenConnectionAsync();

    await using var command = new NpgsqlCommand(
        // TODO can we truncate the timestamp to the start of the month in the database?
        "SELECT trunc(avg(outputpercentage), 2) FROM windfarm_output WHERE windfarmid = $1 and month = $2",
        conn
    ) {
        Parameters = 
        {
            new() { Value = id },
            new() { Value = ts }
        }
    };

    await using var reader = await command.ExecuteReaderAsync();
    await reader.ReadAsync();

    if (reader.HasRows) {
        var data = new List<Object>();
        data.Add(new {
            avgPct = reader.GetDouble(0),
        });

        return Results.Ok(new {
            results = data
        });
    } else {
        return Results.NotFound($"No data for windfarm ID: {id}");
    }
});

app.MapGet("/api/outputforday/{id}/{ts}", async (string id, long ts) => {
    await using var conn = await dataSource.OpenConnectionAsync();

    await using var command = new NpgsqlCommand(
        "SELECT extract(hour from ts) as hour, output, sum(output) OVER (ORDER BY ts ASC) FROM windfarm_output WHERE windfarmid = $1 AND day = $2 ORDER BY hour ASC",
        conn
    ) {
        Parameters = {
            new() { Value = id },
            new() { Value = ts }
        }
    };

    await using var reader = await command.ExecuteReaderAsync();

    var outputs = new List<Object>();

    while (await reader.ReadAsync()) {
        outputs.Add(
            new {
                hour = reader.GetInt32(0),
                output = reader.GetDouble(1),
                cumulativeOutput = double.Parse(reader.GetDouble(2).ToString("N2")) // TODO: Can we do this in SQL?
            }
        );
    }

    return Results.Ok(new {
        results = outputs
    });
});

app.Run();
