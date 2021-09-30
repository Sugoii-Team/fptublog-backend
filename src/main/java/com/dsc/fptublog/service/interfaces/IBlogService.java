package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.BlogEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IBlogService {

    BlogEntity getById(String name) throws SQLException;

    List<BlogEntity> getAllBlogs() throws SQLException;

    BlogEntity createBlog(BlogEntity newBlog) throws SQLException;
}
