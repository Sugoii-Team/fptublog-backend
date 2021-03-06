package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.FieldEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IFieldDAO {

    List<FieldEntity> getByFieldIdList(List<String> fieldIdList) throws SQLException;

    List<FieldEntity> getAll() throws SQLException;

    FieldEntity getById(String id) throws SQLException;

    List<FieldEntity> getTopFields() throws SQLException;

    boolean updateField(FieldEntity updateField) throws SQLException;

    FieldEntity createField(FieldEntity newField) throws SQLException;

    FieldEntity getByName(String name) throws SQLException;
}
