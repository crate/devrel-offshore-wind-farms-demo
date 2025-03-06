package com.cratedb.windfarms.api;

import java.util.Hashtable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WindFarm {
    private String id;
    private String name;
    private String description;
    private Hashtable<String, Double> location;
    private Object boundaries;
    private Object turbines;

    public WindFarm() {
        // Jackson deserialization requires this.
    }

    public WindFarm(String id,String name,String description, Hashtable<String, Double> location, Object boundaries, Object turbines) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.boundaries = boundaries;
        this.turbines = turbines;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public Hashtable<String, Double> getLocation() {
        return location;
    }

    @JsonProperty
    public Object getBoundaries() {
        return boundaries;
    }

    @JsonProperty
    public Object getTurbines() {
        return turbines;
    }
}
