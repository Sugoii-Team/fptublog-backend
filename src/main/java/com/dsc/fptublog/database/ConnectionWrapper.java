package com.dsc.fptublog.database;

import org.jvnet.hk2.annotations.Contract;

import java.sql.Connection;
import java.sql.SQLException;

@Contract
public interface ConnectionWrapper {

    void beginTransaction() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;

    Connection getConnection();
}
