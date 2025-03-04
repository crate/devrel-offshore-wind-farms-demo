package com.cratedb.windfarms.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.cratedb.windfarms.api.LatestStats;
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
    public List<LatestStats> latestForId(@PathParam("id") String id) {
    //public List<Map<String, Object>> latestForId(@PathParam("id") String id) {
        try (Handle h = jdbi.open()) {
            List<LatestStats> stats = h.createQuery(
                "SELECT ts::long as ts, day::long as day, month::long as month, output, outputpercentage FROM windfarm_output WHERE windfarmid = :id ORDER BY ts DESC LIMIT 1"
            ).bind("id", id).map((rs, ctx) -> new LatestStats(
                rs.getLong("ts"), 
                rs.getLong("day"), 
                rs.getLong("month"), 
                rs.getDouble("output"), 
                rs.getDouble("outputpercentage")
            )).list();

            // List<Map<String, Object>> rs = h.createQuery(
            //     "SELECT ts, day, month, output, outputpercentage FROM windfarm_output WHERE windfarmid = :id ORDER BY ts DESC LIMIT 1"
            // ).bind("id", id).mapToMap().list();

            return stats;
        }

        // TODO 404 case.

        // TODO wrap in result object.
   }

    @GET
    @Path("/avgpctformonth/{id}/{ts}")
    public String avgPctForMonth(@PathParam("id") String id, @PathParam("ts") Long ts) {
        return "TODO";
    }

    @GET
    @Path("/api/outputforday/{id}/{ts}")
    public String outputForDay(@PathParam("id") String id, @PathParam("ts") Long ts) {
        return "TODO";
    }

    @GET
    @Path("/api/dailymaxpct/{id}/{days}")
    public String dailyMaxPct(@PathParam("id") String id, @PathParam("days") Long days) {
        return "TODO";
    }
}
