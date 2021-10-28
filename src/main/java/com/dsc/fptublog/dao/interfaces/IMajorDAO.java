package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.MajorEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IMajorDAO {

    MajorEntity getById(String id) throws SQLException;

    MajorEntity getByName(String name) throws SQLException;

    List<MajorEntity> getAll() throws SQLException;
}
