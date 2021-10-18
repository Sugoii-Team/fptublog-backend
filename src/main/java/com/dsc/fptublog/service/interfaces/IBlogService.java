package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.entity.TagEntity;
import com.dsc.fptublog.model.ReviewModel;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IBlogService {

    BlogEntity getById(String name) throws SQLException;

    List<BlogEntity> getAllBlogs(int limit, int page) throws SQLException;

    BlogEntity createBlog(BlogEntity newBlog) throws SQLException;

    List<BlogEntity> getReviewingBlogsOfLecturer(String lecturerId) throws SQLException;

    List<BlogStatusEntity> getAllBlogStatus() throws SQLException;

    BlogStatusEntity getBlogStatus(String id) throws SQLException;

    boolean updateIfNotNull(BlogEntity updatedBlog) throws SQLException;

    boolean updateReviewStatus(ReviewModel reviewModel, String reviewerId, String blogId) throws SQLException;

    List<BlogEntity> getAllBlogsOfAuthor(String authorId) throws SQLException;

    boolean deleteBlogOfAuthor(String authorId, String blogId) throws SQLException;

    BlogEntity updateBlog(String authorId, BlogEntity updatedBlog) throws SQLException;
}
