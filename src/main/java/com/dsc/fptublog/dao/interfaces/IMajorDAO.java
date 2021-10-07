package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.MajorEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IMajorDAO {

    MajorEntity getById(String id) throws SQLException;

    MajorEntity getByName(String name) throws SQLException;
}
