package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.CategoryEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ICategoryService {

    CategoryEntity getCategory(String id) throws SQLException;

    List<CategoryEntity> getCategories() throws SQLException;

    List<CategoryEntity> getCategoriesOfField(String fieldId) throws SQLException;
}
