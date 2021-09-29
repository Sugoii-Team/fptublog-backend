package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.CategoryEntity;

import java.sql.SQLException;

public interface ICategoryService {

    CategoryEntity getCategory(String id) throws SQLException;
}
