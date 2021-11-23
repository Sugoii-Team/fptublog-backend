package com.dsc.fptublog.dao.interfaces;

import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAccountLecturerFieldDAO {
    boolean deleteAccountLecturerField(String fieldId) throws SQLException;
}
