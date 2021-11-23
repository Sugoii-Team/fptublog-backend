package com.dsc.fptublog.config;

import com.dsc.fptublog.filter.AuthFilter;
import com.dsc.fptublog.filter.CORSFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("com.dsc.fptublog.rest");
        register(HK2AutoScanFeature.class);
        register(RolesAllowedDynamicFeature.class);
        register(new CORSFilter());
        register(new AuthFilter());
    }

}
