package com.dsc.fptublog.database;

import org.jvnet.hk2.annotations.Contract;

import java.sql.Connection;
import java.sql.SQLException;

@Contract
public interface ConnectionWrapper {

    public void beginTransaction() throws SQLException;

    public void commit() throws SQLException;

    public void rollback() throws SQLException;

    public void close() throws SQLException;

    public Connection getConnection();
}
