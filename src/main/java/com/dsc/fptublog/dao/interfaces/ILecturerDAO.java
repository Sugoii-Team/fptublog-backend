package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface ILecturerDAO {

    public LecturerEntity getById(String id);

    public LecturerEntity getByEmail(String email);

    public LecturerEntity getByAccount(AccountEntity account) throws SQLException;
}
