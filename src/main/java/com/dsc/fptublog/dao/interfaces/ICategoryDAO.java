package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.CategoryEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;


@Contract
public interface ICategoryDAO {

    public CategoryEntity getById(String id) throws SQLException;
}
