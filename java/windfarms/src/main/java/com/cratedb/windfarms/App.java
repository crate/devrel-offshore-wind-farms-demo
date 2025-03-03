package com.cratedb.windfarms;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

/**
 * Hello world!
 */
public class App extends Application<WindFarmsConfiguration> {
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "windfarms";
    }

    @Override
    public void initialize(Bootstrap<WindFarmsConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(WindFarmsConfiguration configuration, Environment environment) {
        // nothing to do yet
    }
}
