package com.cratedb.windfarms.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaxPercentForDay {
    private long day;
    private double maxOutputPercentage;

    public MaxPercentForDay() {
        // Jackson deserialization requires this.
    }   

    public MaxPercentForDay(long day, double maxOutputPercentage) {
        this.day = day;
        this.maxOutputPercentage = maxOutputPercentage;
    }

    @JsonProperty
    public long getDay() {
        return day;
    }

    @JsonProperty("maxOutputPercentage")
    public double getMaxOutputPercentage() {
        return maxOutputPercentage;
    }
}

