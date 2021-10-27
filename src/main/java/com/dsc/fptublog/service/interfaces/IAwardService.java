package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.AwardEntity;
import com.dsc.fptublog.entity.LecturerStudentAwardEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IAwardService {

    List<AwardEntity> getAwards() throws SQLException;

    AwardEntity getAward(String id) throws SQLException;

    boolean giveAward(LecturerStudentAwardEntity lecturerStudentAward) throws SQLException;

    List<AwardEntity> getAllAwardOfStudent(String studentId) throws SQLException;

    LecturerStudentAwardEntity deleteAwardOfStudent(String id, String lecturerId, String studentId) throws SQLException;
}
