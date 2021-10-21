package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AwardEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IAwardDAO {

    List<AwardEntity> getAllAwards() throws SQLException;

    AwardEntity getById(String id) throws SQLException;
}
