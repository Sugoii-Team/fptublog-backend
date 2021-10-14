package com.dsc.fptublog.listener;

import com.dsc.fptublog.util.ResourcesUtil;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {

    private void configLog4j(ServletContext context) {
        String configFilePath = ResourcesUtil.getAbsolutePath("log4j.properties");

        // set dynamic path for log4j file to WEB-INF
        System.setProperty("PATH", context.getRealPath("/"));
        PropertyConfigurator.configure(configFilePath);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        configLog4j(context);
    }
}
