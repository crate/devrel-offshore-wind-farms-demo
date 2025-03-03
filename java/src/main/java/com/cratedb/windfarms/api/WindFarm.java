package com.cratedb.windfarms.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WindFarm {
    private String id;
    private String name;
    private String description;

    public WindFarm() {
        // Jackson deserialization requires this.
    }

    public WindFarm(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}
