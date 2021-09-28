package com.dsc.fptublog.dao.interfaces;


import com.dsc.fptublog.entity.BlogStatusEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;


@Contract
public interface IBlogStatusDAO {
    public BlogStatusEntity getById(String id) throws SQLException;

    public boolean deleteById(String deletedBlogStatusId) throws SQLException;

    public boolean updateByBlogStatus(BlogStatusEntity updatedBlogStatus) throws SQLException;

    public BlogStatusEntity insertByBlogStatus(BlogStatusEntity blogStatus) throws SQLException;
}
