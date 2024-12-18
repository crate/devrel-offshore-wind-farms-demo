using System.Text.Json;
using Npgsql;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllersWithViews();

var app = builder.Build();
app.UseFileServer();

// TODO consider trapping a null here.
await using var dataSource = NpgsqlDataSource.Create(app.Configuration.GetConnectionString("CrateDB")!);

app.MapGet("/testing", async () => {
    await using var command = dataSource.CreateCommand("SELECT id, name, description, boundaries, turbines FROM windfarms ORDER BY id ASC");
    await using var reader = await command.ExecuteReaderAsync();

    // TODO can the database driver help with this?
    var windFarms = new Dictionary<string, Object>();

    while (await reader.ReadAsync()) {
        var coords = reader.GetFieldValue<JsonDocument>(3);
        Console.WriteLine(coords);
        windFarms.Add(reader.GetString(0), new {
            name = reader.GetString(1),
            boundaries = reader.GetFieldValue<JsonDocument>(3),
            turbines = reader.GetFieldValue<JsonDocument>(4)
        });
    }

    return new {
        results = windFarms
    };
});

app.Run();
