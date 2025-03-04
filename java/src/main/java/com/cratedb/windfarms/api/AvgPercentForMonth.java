package com.cratedb.windfarms.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvgPercentForMonth {
    private double avgPct;

    public AvgPercentForMonth() {
        // Jackson deserialization requires this.
    }

    public AvgPercentForMonth(double avgPct) {
        this.avgPct = avgPct;
    }

    @JsonProperty("avgPct")
    public double getAvgPct() {
        return avgPct;
    }
}
