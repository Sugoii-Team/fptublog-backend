package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.CategoryEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ICategoryDAO {

    CategoryEntity getById(String id) throws SQLException;

    List<CategoryEntity> getAll() throws SQLException;

    List<CategoryEntity> getByFieldIdList(List<String> fieldIdList) throws SQLException;

    List<CategoryEntity> getByFieldId(String fieldId) throws SQLException;

    boolean updateCategory(CategoryEntity updateCategory) throws SQLException;

    CategoryEntity createCategory(CategoryEntity newCategory) throws SQLException;

    boolean deleteCategory(CategoryEntity category) throws SQLException;

    CategoryEntity getByName(String name) throws SQLException;
}
