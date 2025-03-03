package com.cratedb.windfarms;

import com.cratedb.windfarms.resources.WindFarmsResource;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class windfarmsApplication extends Application<windfarmsConfiguration> {

    public static void main(final String[] args) throws Exception {
        new windfarmsApplication().run(args);
    }

    @Override
    public String getName() {
        return "windfarms";
    }

    @Override
    public void initialize(final Bootstrap<windfarmsConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.htm", "root"));
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
    }

    @Override
    public void run(final windfarmsConfiguration configuration,
                    final Environment environment) {
        WindFarmsResource windFarmsRes = new WindFarmsResource();
        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(windFarmsRes);
    }

}
