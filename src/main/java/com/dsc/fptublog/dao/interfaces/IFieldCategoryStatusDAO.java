package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.FieldCategoryStatusEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IFieldCategoryStatusDAO {

    FieldCategoryStatusEntity getById(String id) throws SQLException;

    FieldCategoryStatusEntity getByName(String name) throws SQLException;
}
