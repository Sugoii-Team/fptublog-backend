package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.LecturerStudentAwardEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ILecturerStudentAwardDAO {

    boolean insertByLecturerStudentAward(LecturerStudentAwardEntity lecturerStudentAward) throws SQLException;

    List<LecturerStudentAwardEntity> getByStudentId(String studentId) throws SQLException;

    LecturerStudentAwardEntity getById(String id) throws SQLException;

    boolean deleteById(String id) throws SQLException;

    boolean deleteByStudentId(String studentId) throws SQLException;

    boolean deleteByLecturerId(String lecturerId) throws SQLException;
}
