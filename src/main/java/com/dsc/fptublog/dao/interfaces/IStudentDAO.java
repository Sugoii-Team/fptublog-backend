package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.model.Top30DaysStudentModel;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Contract
public interface IStudentDAO {

    List<StudentEntity> getAll() throws SQLException;

    StudentEntity createFromAccount(AccountEntity account, String majorId) throws SQLException;

    StudentEntity insertByAccountIdAndMajorIdAndSchoolYear(String accountId, String majorId, short schoolYear) throws SQLException;

    StudentEntity getByAccount(AccountEntity account) throws SQLException;

    boolean updateStudent(StudentEntity studentEntity) throws SQLException;

    boolean deleteStudentById(String id) throws SQLException;

    List<Top30DaysStudentModel> getTop30Days(long before30DaysTimeStamp) throws SQLException;

    StudentEntity getById(String id) throws SQLException;

}
