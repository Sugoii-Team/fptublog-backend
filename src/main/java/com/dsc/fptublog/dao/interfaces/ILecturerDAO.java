package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ILecturerDAO {

    List<LecturerEntity> getAll() throws SQLException;

    LecturerEntity getById(String id) throws SQLException;

    LecturerEntity getByEmail(String email) throws SQLException;

    LecturerEntity getByAccount(AccountEntity account) throws SQLException;

    LecturerEntity insertById(String id) throws SQLException;

    boolean deleteById(String id) throws SQLException;
}
