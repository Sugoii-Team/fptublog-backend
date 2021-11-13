package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.model.Top30DaysStudentModel;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IStudentService {

    List<StudentEntity> getAllStudents() throws SQLException;

    StudentEntity getStudent(String id) throws SQLException;

    StudentEntity updateStudent(StudentEntity studentEntity) throws SQLException;

    List<Top30DaysStudentModel> getTopIn30Days() throws SQLException;
}
