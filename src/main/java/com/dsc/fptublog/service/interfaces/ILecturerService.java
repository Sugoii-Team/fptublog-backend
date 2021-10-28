package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.LecturerEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ILecturerService {

    List<LecturerEntity> getAllLecturers() throws SQLException;

    LecturerEntity getLecturer(String id) throws SQLException;

    boolean banStudent(String studentId) throws SQLException;

    boolean unbanStudent(String studentId) throws SQLException;
}
