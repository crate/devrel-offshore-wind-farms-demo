package com.cratedb.windfarms.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatestStatsResults {
    private List<LatestStats> results;

    public LatestStatsResults() {
        // Jackson deserialization requires this.
    }

    public LatestStatsResults(List<LatestStats> results) {
        this.results = results;
    }

    @JsonProperty
    public List<LatestStats> getResults() {
        return results;
    }
}