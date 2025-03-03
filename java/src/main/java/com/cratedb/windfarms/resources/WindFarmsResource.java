package com.cratedb.windfarms.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.cratedb.windfarms.api.WindFarm;

import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WindFarmsResource {
    public WindFarmsResource() {

    }

    @GET
    @Path("/windfarms")
    public List<WindFarm> windfarms() {
        ArrayList<WindFarm> windFarms = new ArrayList<WindFarm>();

        // TODO Get real values from the database!
        // TODO how to return in a results array...
        WindFarm wf = new WindFarm("TEES-1", "Test Name", "This is the description.");
        windFarms.add(wf);
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
