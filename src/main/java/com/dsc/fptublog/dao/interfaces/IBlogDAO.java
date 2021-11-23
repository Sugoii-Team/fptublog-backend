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

    List<BlogEntity> getAllBlogs(int limit, int offset) throws SQLException;

    List<BlogEntity> getByTitle(int limit, int offset, String title) throws SQLException;

    List<BlogEntity> getTopBlogs(int limit, int offset) throws SQLException;

    List<BlogEntity> getPendingBlogByCategoryIdList(List<String> categoryIdList) throws SQLException;

    List<BlogEntity> getApprovedBlogByFieldId(String fieldId, int limit, int offset) throws SQLException;

    BlogEntity blogIdIsExistent(String blogId) throws SQLException;

    List<BlogEntity> getByAuthorId(String authorId, int limit, int offset) throws SQLException;

    List<BlogEntity> getByAuthorId(String authorId, int limit, int offset, String sortByField, String orderByType)
            throws SQLException;

    List<BlogEntity> getApprovedBlogByAuthorId(String authorId, int limit, int offset) throws SQLException;

    List<BlogEntity> getApprovedBlogByAuthorId(String authorId, int limit, int offset, String sortByField,
                                               String orderByType) throws SQLException;

    boolean hideBlogInHistory(String blogHistoryId) throws SQLException;

    boolean deleteReviewerId(String reviewerId) throws SQLException;

    List<BlogEntity> getByCategoryId(String categoryId, int limit, int offset) throws SQLException;

    boolean isApproved(String blogId) throws SQLException;

    boolean isExistedPendingUpdateBlogInTheSameBlogHistory(String blogId) throws SQLException;

    String getPendingUpdateBlogIdInTheSameHistory(String blogId) throws SQLException;

    List<BlogEntity> getByTagId(String tagId, int limit, int offset) throws SQLException;
}
