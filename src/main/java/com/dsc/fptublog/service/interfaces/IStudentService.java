package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.StudentEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IStudentService {
    public StudentEntity getStudent(String id) throws SQLException;

    public StudentEntity updateStudent(StudentEntity studentEntity) throws SQLException;
}
