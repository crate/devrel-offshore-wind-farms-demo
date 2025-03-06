package com.cratedb.windfarms.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutputForDay {
    private int hour;
    private double output;
    private double cumulativeOutput;

    public OutputForDay() {
        // Jackson deserialization requires this.
    }

    public OutputForDay(int hour, double output, double cumulativeOutput) {
        this.hour = hour;
        this.output = output;
        this.cumulativeOutput = cumulativeOutput;
    }

    @JsonProperty
    public int getHour() {
        return hour;
    }

    @JsonProperty
    public double getOutput() {
        return output;
    }

    @JsonProperty("cumulativeOutput")
    public double getCumulativeOutput() {
        return cumulativeOutput;
    }
}
