package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.StudentEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IStudentDAO {

    StudentEntity createFromAccount(AccountEntity account, String majorId) throws SQLException;

    StudentEntity getByAccount(AccountEntity account) throws SQLException;

}
