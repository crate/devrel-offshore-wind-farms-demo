package com.cratedb.windfarms.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import com.cratedb.windfarms.api.AvgPercentForMonth;
import com.cratedb.windfarms.api.AvgPercentForMonthResults;
import com.cratedb.windfarms.api.LatestStats;
import com.cratedb.windfarms.api.LatestStatsResults;
import com.cratedb.windfarms.api.MaxPercentForDay;
import com.cratedb.windfarms.api.MaxPercentForDayResults;
import com.cratedb.windfarms.api.OutputForDay;
import com.cratedb.windfarms.api.OutputForDayResults;
import com.cratedb.windfarms.api.WindFarm;
import com.cratedb.windfarms.api.WindFarmResults;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import org.postgresql.geometric.PGpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WindFarmsResource {
    private Jdbi jdbi;

    public WindFarmsResource(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GET
    @Path("/windfarms")
    public WindFarmResults windfarms() throws JsonProcessingException {
        List<WindFarm> windFarms = new ArrayList<WindFarm>();

        try (Handle h = jdbi.open()) {
            List<Map<String, Object>> rs = h.createQuery("SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC").mapToMap().list();

            for (Map<String, Object> row : rs) {
                Hashtable<String, Double> loc = new Hashtable<String, Double>();
                PGpoint pt = (PGpoint)row.get("location");

                loc.put("x", pt.x);
                loc.put("y", pt.y);

                ObjectMapper m = new ObjectMapper();
                JsonNode boundaries = m.readTree(row.get("boundaries").toString());
                JsonNode turbines = m.readTree(row.get("turbines").toString());
            
                WindFarm wf = new WindFarm(
                    row.get("id").toString(), 
                    row.get("name").toString(), 
                    row.get("description").toString(),
                    loc,
                    boundaries,
                    turbines
                );

                windFarms.add(wf);
            }

            h.close();
        }

        // TODO 404 case...

        return new WindFarmResults(windFarms);
    }

    @GET
    @Path("/latest/{id}")
    public LatestStatsResults latestForId(@PathParam("id") String id) {
        List<LatestStats> stats = new ArrayList<LatestStats>();

        try (Handle h = jdbi.open()) {
            stats = h.createQuery(
                "SELECT ts::long as ts, day::long as day, month::long as month, output, outputpercentage FROM windfarm_output WHERE windfarmid = :id ORDER BY ts DESC LIMIT 1"
            ).bind("id", id).map((rs, ctx) -> new LatestStats(
                rs.getLong("ts"), 
                rs.getLong("day"), 
                rs.getLong("month"), 
                rs.getDouble("output"), 
                rs.getDouble("outputpercentage")
            )).list();
        }

        if (stats.size() == 0) {
            throw new WebApplicationException("No such windfarm ID " + id + ".", 404);
        }

        return new LatestStatsResults(stats);
   }

    @GET
    @Path("/avgpctformonth/{id}/{ts}")
    public AvgPercentForMonthResults avgPctForMonth(@PathParam("id") String id, @PathParam("ts") Long ts) {
        AvgPercentForMonth result;

        try (Handle h = jdbi.open()) {
            Double avgPct = h.createQuery(
                "SELECT trunc(avg(outputpercentage), 2) FROM windfarm_output WHERE windfarmid = :id and month = :ts"
            ).bind("id", id).bind("ts", ts).mapTo(Double.class).first();

            result = new AvgPercentForMonth(avgPct);
        }

        // TODO 404 case.

        return new AvgPercentForMonthResults(result);
    }

    @GET
    @Path("/outputforday/{id}/{ts}")
    public OutputForDayResults outputForDay(@PathParam("id") String id, @PathParam("ts") Long ts) {
        List<OutputForDay> outputsForDay = new ArrayList<OutputForDay>();

        try (Handle h = jdbi.open()) {
            outputsForDay = h.createQuery(
                "SELECT extract(hour from ts) AS hour, output, sum(output) OVER (ORDER BY ts ASC) AS cumulativeoutput FROM windfarm_output WHERE windfarmid = :id AND day = :ts ORDER BY hour ASC"
            ).bind("id", id).bind("ts", ts).map((rs, ctx) -> new OutputForDay(
                rs.getInt("hour"), 
                rs.getDouble("output"),  
                Math.round(rs.getDouble("cumulativeoutput") * Math.pow(10, 2)) / Math.pow(10,2)
            )).list();
        }

        // TODO 404 case.

        return new OutputForDayResults(outputsForDay);
    }

    @GET
    @Path("/dailymaxpct/{id}/{days}")
    public MaxPercentForDayResults dailyMaxPct(@PathParam("id") String id, @PathParam("days") Long days) {
        List<MaxPercentForDay> maxForDays = new ArrayList<MaxPercentForDay>();

        try (Handle h = jdbi.open()) {
            maxForDays = h.createQuery(
                "SELECT day::long AS day, max(outputpercentage) AS maxoutputpct FROM windfarm_output WHERE windfarmid = :id GROUP BY day ORDER BY day DESC LIMIT :days"
            ).bind("id", id).bind("days", days).map((rs, ctx) -> new MaxPercentForDay(
                rs.getLong("day"), 
                rs.getDouble("maxoutputpct")
            )).list();
        }

        // TODO 404 case.

        return new MaxPercentForDayResults(maxForDays);
    }
}
