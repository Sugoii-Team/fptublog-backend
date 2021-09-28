package com.dsc.fptublog.config;

import lombok.extern.log4j.Log4j;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.DuplicatePostProcessor;
import org.glassfish.jersey.internal.inject.InjectionManager;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.io.IOException;

@Log4j
public class HK2AutoScanFeature implements Feature {

    @Inject
    private InjectionManager injectionManager;

    @Override
    public boolean configure(FeatureContext context) {
        DynamicConfigurationService dcs =
                injectionManager.getInstance(DynamicConfigurationService.class);
        Populator populator = dcs.getPopulator();
        try {
            // Populator - populate HK2 service locators from inhabitants files
            // ClasspathDescriptorFileFinder - find files from META-INF/hk2-locator/default
            populator.populate(
                    new ClasspathDescriptorFileFinder(this.getClass().getClassLoader()),
                    new DuplicatePostProcessor());

        } catch (IOException | MultiException ex) {
            log.error(ex);
        }
        return true;
    }
}
