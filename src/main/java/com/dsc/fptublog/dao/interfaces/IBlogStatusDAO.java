package com.dsc.fptublog.dao.interfaces;


import com.dsc.fptublog.entity.BlogStatusEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;


@Contract
public interface IBlogStatusDAO {
    BlogStatusEntity getById(String id) throws SQLException;

    BlogStatusEntity getByName(String name) throws SQLException;

    boolean deleteById(String deletedBlogStatusId) throws SQLException;

    boolean updateByBlogStatus(BlogStatusEntity updatedBlogStatus) throws SQLException;

    BlogStatusEntity insertByBlogStatus(BlogStatusEntity blogStatus) throws SQLException;
}
