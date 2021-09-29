package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.CategoryEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface ICategoryService {

    CategoryEntity getCategory(String id) throws SQLException;
}
