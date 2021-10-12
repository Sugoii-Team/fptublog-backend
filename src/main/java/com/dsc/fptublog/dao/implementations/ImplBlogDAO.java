package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequestScoped
public class ImplBlogDAO implements IBlogDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public BlogEntity insertByBlog(BlogEntity newBlog) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "INSERT INTO blog (author_id, thumbnail_url, title, content, description, created_datetime, " +
                "status_id, category_id, reviewer_id, review_datetime, views) "
                + "OUTPUT inserted.id "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, newBlog.getAuthorId());
            stm.setString(2, newBlog.getThumbnailUrl());
            stm.setNString(3, newBlog.getTitle());
            stm.setNString(4, newBlog.getContent());
            stm.setNString(5, newBlog.getDescription());
            stm.setLong(6, newBlog.getCreatedDateTime());
            stm.setString(7, newBlog.getStatusId());
            stm.setString(8, newBlog.getCategoryId());
            stm.setString(9, newBlog.getReviewerId());
            stm.setLong(10, newBlog.getReviewDateTime());
            stm.setInt(11, newBlog.getViews());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                newBlog.setId(resultSet.getString(1));
                return newBlog;
            }
        }

        return null;
    }

    @Override
    public boolean updateByBlog(BlogEntity updatedBlog) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE blog "
                + "SET thumbnail_url = ISNULL(?, thumbnail_url) , title = ISNULL(?, title), " +
                "content = ISNULL(?, content), description = ISNULL(?, description), " +
                "status_id = ISNULL(?, status_id), category_id = ISNULL(?, category_id), " +
                "reviewer_id = ISNULL(?, reviewer_id), review_datetime = ISNULL(?, review_datetime), " +
                "views = ISNULL(?, views) "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, updatedBlog.getThumbnailUrl());
            stm.setNString(2, updatedBlog.getTitle());
            stm.setNString(3, updatedBlog.getContent());
            stm.setNString(4, updatedBlog.getDescription());
            stm.setString(5, updatedBlog.getStatusId());
            stm.setString(6, updatedBlog.getCategoryId());
            stm.setString(7, updatedBlog.getReviewerId());
            stm.setLong(8, updatedBlog.getReviewDateTime());
            stm.setInt(9, updatedBlog.getViews());
            stm.setString(10, updatedBlog.getId());

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlogEntity getById(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        BlogEntity result = null;

        String sql = "SELECT author_id, thumbnail_url, title, content, description, created_datetime, status_id, " +
                "category_id, reviewer_id, review_datetime, views " +
                "FROM blog " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String authorId = resultSet.getString(1);
                String thumbnailUrl = resultSet.getString(2);
                String title = resultSet.getNString(3);
                String content = resultSet.getNString(4);
                String desciption = resultSet.getNString(5);
                long createdDatetime = resultSet.getLong(6);
                String statusId = resultSet.getString(7);
                String categoryId = resultSet.getString(8);
                String reviewerId = resultSet.getString(9);
                long reviewDatetime = resultSet.getLong(10);
                int views = resultSet.getInt(11);

                result = new BlogEntity(blogId, authorId, thumbnailUrl, title, content, desciption, createdDatetime,
                        statusId, categoryId, reviewerId, reviewDatetime, views);
            }
        }

        return result;
    }

    @Override
    public boolean deletedById(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE " +
                "FROM blog " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<BlogEntity> getAllBlogs() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT id, author_id, thumbnail_url, title, description, created_datetime, status_id, " +
                "category_id, reviewer_id, review_datetime, views " +
                "FROM blog";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String authorId = resultSet.getString(2);
                String thumbnailUrl = resultSet.getString(3);
                String title = resultSet.getNString(4);
                String description = resultSet.getNString(5);
                long createdDatetime = resultSet.getLong(6);
                String statusId = resultSet.getString(7);
                String categoryId = resultSet.getString(8);
                String reviewerId = resultSet.getString(9);
                long reviewDatetime = resultSet.getLong(10);
                int views = resultSet.getInt(11);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new BlogEntity(id, authorId, thumbnailUrl, title, null, description,
                        createdDatetime, statusId, categoryId, reviewerId, reviewDatetime, views));
            }
        }

        return result;
    }

    @Override
    public List<BlogEntity> getByCategoryIdList(List<String> categoryIdList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT id, author_id, thumbnail_url, title, description, created_datetime, status_id, " +
                "reviewer_id, review_datetime, views " +
                "FROM blog " +
                "WHERE category_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (var categoryId : categoryIdList) {
                stm.setString(1, categoryId);

                ResultSet resultSet = stm.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString(1);
                    String authorId = resultSet.getString(2);
                    String thumbnailUrl = resultSet.getString(3);
                    String title = resultSet.getNString(4);
                    String description = resultSet.getNString(5);
                    long createdDatetime = resultSet.getLong(6);
                    String statusId = resultSet.getString(7);
                    String reviewerId = resultSet.getString(8);
                    long reviewDatetime = resultSet.getLong(9);
                    int views = resultSet.getInt(10);

                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(new BlogEntity(id, authorId, thumbnailUrl, title, null, description,
                            createdDatetime, statusId, categoryId, reviewerId, reviewDatetime, views));
                }
            }
        }

        return result;
    }

    @Override
    public List<BlogEntity> getByAuthorId(String authorId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT id, thumbnail_url, title, description, created_datetime, status_id, category_id, " +
                "reviewer_id, review_datetime, views " +
                "FROM blog " +
                "WHERE author_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, authorId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String thumbnailUrl = resultSet.getString(2);
                String title = resultSet.getNString(3);
                String description = resultSet.getNString(4);
                long createdDatetime = resultSet.getLong(5);
                String statusId = resultSet.getString(6);
                String categoryId = resultSet.getString(7);
                String reviewerId = resultSet.getString(8);
                long reviewDatetime = resultSet.getLong(9);
                int views = resultSet.getInt(10);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new BlogEntity(id, authorId, thumbnailUrl, title, null, description, createdDatetime, statusId,
                        categoryId, reviewerId, reviewDatetime, views));
            }
        }

        return result;
    }
}
