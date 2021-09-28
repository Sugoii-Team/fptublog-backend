package com.dsc.fptublog.database;

import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A wrapper class for java.sql.Connection to management Transaction
 * and implement IoC for DAO tier
 */
@Service
@RequestScoped
public class ImplConnectionWrapper implements ConnectionWrapper {

    private Connection connection;

    @Override
    public void beginTransaction() throws SQLException {
        this.connection = DataSource.getConnection();
    }

    @Override
    public void commit() throws SQLException {
        this.connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        this.connection.close();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

}
