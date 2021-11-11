package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogHistoryDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogHistory;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequestScoped
public class ImplBlogHistoryDAO implements IBlogHistoryDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;


    @Override
    public BlogHistory insertByBlogHistory(BlogHistory blogHistory) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "INSERT INTO blog_history (author_id, category_id, created_datetime, views, avg_rate) " +
                "OUTPUT inserted.id " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogHistory.getAuthorId());
            stm.setString(2, blogHistory.getCategoryId());
            stm.setLong(3, blogHistory.getCreatedDatetime());
            stm.setInt(4, blogHistory.getViews());
            stm.setFloat(5, blogHistory.getAvgRate());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                blogHistory.setId(id);

                return blogHistory;
            }
        }

        return null;
    }

    @Override
    public BlogHistory getByBlogId(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT blog_history.id, author_id, category_id, blog.created_datetime, views, avg_rate " +
                "FROM blog_history " +
                "INNER JOIN blog on blog_history.id = blog.blog_history_id " +
                "WHERE blog.id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString(1);
                String authorId = resultSet.getString(2);
                String categoryId = resultSet.getString(3);
                long createdDatetime = resultSet.getLong(4);
                int views = resultSet.getInt(5);
                float avgRate = resultSet.getFloat(6);

                return new BlogHistory(id, authorId, categoryId, createdDatetime, views, avgRate);
            }
        }

        return null;
    }
}
