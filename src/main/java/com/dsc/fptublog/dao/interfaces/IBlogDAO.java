package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IBlogDAO {

    BlogEntity insertByBlog(BlogEntity newBlog) throws SQLException;

    boolean updateByBlog(BlogEntity updatedBlog) throws SQLException;

    BlogEntity getById(String blogId) throws SQLException;

    boolean deletedById(String blogId) throws SQLException;

    List<BlogEntity> getAllBlogs() throws SQLException;

    /*List<BlogEntity> getByCategoryIdList(List<String> categoryIdList) throws SQLException;*/

    List<BlogEntity> getPendingBlogByCategoryIdList(List<String> categoryIdList) throws SQLException;

    List<BlogEntity> getByAuthorId(String authorId) throws SQLException;
}
