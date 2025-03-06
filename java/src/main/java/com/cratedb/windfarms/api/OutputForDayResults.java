package com.cratedb.windfarms.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutputForDayResults {
    private List<OutputForDay> results;

    public OutputForDayResults() {
        // Jackson deserialization requires this.
    }

    public OutputForDayResults(List<OutputForDay> results) {
        this.results = results;
    }

    @JsonProperty
    public List<OutputForDay> getResults() {
        return results;
    }
}