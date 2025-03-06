package com.cratedb.windfarms.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatestStats {
    private long timestamp;
    private long day;
    private long month;
    private double output;
    private double outputPercentage;

    public LatestStats() {
        // Jackson deserialization requires this.
    }   

    public LatestStats(long timestamp, long day, long month, double output, double outputPercentage) {
        this.timestamp = timestamp;
        this.day = day;
        this.month = month;
        this.output = output;
        this.outputPercentage = outputPercentage;
    }

    @JsonProperty
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty
    public long getDay() {
        return day;
    }

    @JsonProperty
    public long getMonth() {
        return month;
    }

    @JsonProperty
    public double getOutput() {
        return output;
    }

    @JsonProperty("outputPercentage")
    public double getOutputPercentage() {
        return outputPercentage;
    }
}
