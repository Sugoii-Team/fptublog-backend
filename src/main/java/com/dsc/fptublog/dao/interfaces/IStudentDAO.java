package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.StudentEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IStudentDAO {

    public StudentEntity createByStudent(StudentEntity studentEntity) throws SQLException;

    public StudentEntity getById(String id) throws SQLException;

    public StudentEntity getByEmail(String email) throws SQLException;

    public StudentEntity getByAccount(AccountEntity account) throws SQLException;

    public boolean updateByStudent(StudentEntity studentEntity);

    public StudentEntity deleteById(String id);
}
