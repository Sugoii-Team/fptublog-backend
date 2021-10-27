package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.MajorEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IMajorService {

    List<MajorEntity> getAllMajor() throws SQLException;

    MajorEntity getMajor(String id) throws SQLException;
}
