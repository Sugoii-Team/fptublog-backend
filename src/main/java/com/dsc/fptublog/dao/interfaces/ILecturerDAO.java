package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface ILecturerDAO {

    LecturerEntity getById(String id) throws SQLException;

    LecturerEntity getByEmail(String email) throws SQLException;

    LecturerEntity getByAccount(AccountEntity account) throws SQLException;
}
