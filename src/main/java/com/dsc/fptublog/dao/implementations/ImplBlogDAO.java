package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.*;
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

        String sql = "INSERT INTO blog (author_id, title, content, created_datetime, status_id, category_id, reviewer_id, review_datetime, views) "
                + "OUTPUT inserted.id "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, newBlog.getAuthorId());
            stm.setNString(2, newBlog.getTitle());
            stm.setNString(3, newBlog.getContent());
            stm.setLong(4, newBlog.getCreatedDateTime());
            stm.setString(5, newBlog.getStatusId());
            stm.setString(6, newBlog.getCategoryId());
            stm.setString(7, newBlog.getReviewerId());
            stm.setLong(8, newBlog.getReviewDateTime());
            stm.setInt(9, newBlog.getViews());

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
                + "SET title = ?, content = ?, status_id = ?, category_id = ?, reviewer_id = ?, review_datetime = ?, views = ? "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setNString(1, updatedBlog.getTitle());
            stm.setNString(2, updatedBlog.getContent());
            stm.setString(3, updatedBlog.getStatusId());
            stm.setString(4, updatedBlog.getCategoryId());
            stm.setString(5, updatedBlog.getReviewerId());
            stm.setLong(6, updatedBlog.getReviewDateTime());
            stm.setInt(7, updatedBlog.getViews());

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

        String sql = "SELECT author_id, title, content, created_datetime, status_id, category_id, reviewer_id, review_datetime, views "
                + "FROM blog "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String authorId = resultSet.getString(1);
                String title = resultSet.getNString(2);
                String content = resultSet.getNString(3);
                long createdDatetime = resultSet.getLong(4);
                String statusId = resultSet.getString(5);
                String categoryId = resultSet.getString(6);
                String reviewerId = resultSet.getString(7);
                long reviewDatetime = resultSet.getLong(8);
                int views = resultSet.getInt(9);

                result = new BlogEntity(blogId, authorId, title, content, createdDatetime,
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

        String sql = "SELECT id, author_id, title, content, created_datetime, status_id, category_id, reviewer_id, review_datetime, views "
                + "FROM blog";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String authorId = resultSet.getString(2);
                String title = resultSet.getNString(3);
                String content = resultSet.getNString(4);
                long createdDatetime = resultSet.getLong(5);
                String statusId = resultSet.getString(6);
                String categoryId = resultSet.getString(7);
                String reviewerId = resultSet.getString(8);
                long reviewDatetime = resultSet.getLong(9);
                int views = resultSet.getInt(10);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new BlogEntity(id, authorId, title, content, createdDatetime,
                        statusId, categoryId, reviewerId, reviewDatetime, views));
            }
        }

        return result;
    }
}
