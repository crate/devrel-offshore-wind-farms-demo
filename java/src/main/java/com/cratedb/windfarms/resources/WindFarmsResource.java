package com.cratedb.windfarms.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.cratedb.windfarms.api.WindFarm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;

import org.postgresql.geometric.PGpoint;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WindFarmsResource {
    private Jdbi jdbi;

    public WindFarmsResource(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @GET
    @Path("/windfarms")
    public List<WindFarm> windfarms() {
        List<WindFarm> windFarms = new ArrayList<WindFarm>();

        Handle h = jdbi.open();
        List<Map<String, Object>> rs = h.createQuery("SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC").mapToMap().list();

        for (Map<String, Object> row : rs) {
            System.out.println(row.get("id"));
            System.out.println(row.get("name"));
            System.out.println(row.get("description"));
            System.out.println(row.get("location"));
            System.out.println(row.get("boundaries"));
            System.out.println(row.get("turbines"));

            Hashtable<String, Double> loc = new Hashtable<String, Double>();
            PGpoint pt = (PGpoint)row.get("location");

            loc.put("x", pt.x);
            loc.put("y", pt.y);
        
            WindFarm wf = new WindFarm(
                row.get("id").toString(), 
                row.get("name").toString(), 
                row.get("description").toString(),
                loc,
                row.get("boundaries"),
                row.get("turbines")
            );

            windFarms.add(wf);
        }
        // TODO do some formatting.

        // TODO ensure this always happens.
        h.close();

        return windFarms;
    }

    @GET
    @Path("/latest/{id}")
    public String latestForId(@PathParam("id") String id) {
        return id;
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
