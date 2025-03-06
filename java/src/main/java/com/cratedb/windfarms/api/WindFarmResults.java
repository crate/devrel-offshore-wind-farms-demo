package com.cratedb.windfarms.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WindFarmResults {
    private List<WindFarm> results;

    public WindFarmResults() {
        // Jackson deserialization requires this.
    }

    public WindFarmResults(List<WindFarm> results) {
        this.results = results;
    }

    @JsonProperty
    public List<WindFarm> getResults() {
        return results;
    }
}
