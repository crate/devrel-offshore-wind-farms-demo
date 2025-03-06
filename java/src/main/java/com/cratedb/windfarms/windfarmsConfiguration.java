package com.cratedb.windfarms;

import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class windfarmsConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
