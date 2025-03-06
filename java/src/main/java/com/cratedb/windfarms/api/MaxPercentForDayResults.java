package com.cratedb.windfarms.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaxPercentForDayResults {
    private List<MaxPercentForDay> results;

    public MaxPercentForDayResults() {
        // Jackson deserialization requires this.
    }

    public MaxPercentForDayResults(List<MaxPercentForDay> results) {
        this.results = results;
    }

    @JsonProperty
    public List<MaxPercentForDay> getResults() {
        return results;
    }
}
