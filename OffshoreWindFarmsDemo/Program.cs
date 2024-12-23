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

    return new {
        results = windFarms
    };
});

app.MapGet("/api/latest/{id}", async (string id) => {
    await using var conn = await dataSource.OpenConnectionAsync();

    await using var command = new NpgsqlCommand(
        "SELECT ts, output, outputpercentage FROM windfarm_output WHERE windfarmid = $1 ORDER BY ts DESC LIMIT 1",
        conn
    ) {
        Parameters = 
        {
            new() { Value = id }
        }
    };
        
    await using var reader = await command.ExecuteReaderAsync();
    await reader.ReadAsync();

    return new {
        results = new {
            timestamp = ((DateTimeOffset)reader.GetDateTime(0)).ToUnixTimeMilliseconds(),
            output = reader.GetDouble(1),
            outputPercentage = reader.GetDouble(2) 
        }
    };
});

app.Run();
