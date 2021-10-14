package com.dsc.fptublog.database;

import com.dsc.fptublog.util.ResourcesUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jvnet.hk2.annotations.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DataSource {

    private static final String CONFIG_FILE_PATH = ResourcesUtil.getAbsolutePath("db.properties");
    private static final HikariConfig CONFIG = new HikariConfig(CONFIG_FILE_PATH);
    private static final HikariDataSource DATA_SOURCE = new HikariDataSource(CONFIG);

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
