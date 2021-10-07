package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.LecturerAwardEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ILecturerAwardDAO {

    List<LecturerAwardEntity> getByLecturerId(String lecturerId) throws SQLException;

}
