package com.cratedb.windfarms.api;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvgPercentForMonthResults {
    private List<AvgPercentForMonth> results;

    public AvgPercentForMonthResults() {
        // Jackson deserialization requires this.
    }

    public AvgPercentForMonthResults(AvgPercentForMonth r) {
        results = new ArrayList<AvgPercentForMonth>();
        results.add(r);
    }

    @JsonProperty
    public List<AvgPercentForMonth> getResults() {
        return results;
    }
}
