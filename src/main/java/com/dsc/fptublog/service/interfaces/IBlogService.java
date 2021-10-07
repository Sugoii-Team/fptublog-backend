package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.entity.TagEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IBlogService {

    BlogEntity getById(String name) throws SQLException;

    List<BlogEntity> getAllBlogs() throws SQLException;

    BlogEntity createBlog(BlogEntity newBlog) throws SQLException;

    boolean createTagListForBlog(String blogId, List<TagEntity> tagList) throws SQLException;

    List<BlogEntity> getReviewingBlogsOfLecturer(String lecturerId) throws SQLException;

    List<BlogStatusEntity> getAllBlogStatus() throws SQLException;

    boolean updateIfNotNull(BlogEntity updatedBlog) throws SQLException;

    boolean updateReviewStatus(BlogEntity updatedBlog) throws SQLException;
}
