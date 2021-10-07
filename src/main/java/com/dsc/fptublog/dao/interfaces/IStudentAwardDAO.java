package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.StudentAwardEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IStudentAwardDAO {

    List<StudentAwardEntity> getByStudentId(String studentId) throws SQLException;
}
