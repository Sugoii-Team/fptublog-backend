package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.FieldEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IFieldDAO {

    List<FieldEntity> getByFieldIdList(List<String> fieldIdList) throws SQLException;

    List<FieldEntity> getAll() throws SQLException;
}
