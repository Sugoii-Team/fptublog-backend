package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IBlogDAO {
    public BlogEntity insertByBlog(BlogEntity newBlog) throws SQLException;

    public boolean updateByBlog(BlogEntity updatedBlog) throws SQLException;

    public BlogEntity getById(String blogId) throws SQLException;

    public boolean deletedById(String blogId) throws SQLException;

    public List<BlogEntity> getAllBlogs() throws SQLException;
}
