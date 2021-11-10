package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogRateDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogRateEntity;
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
public class ImplBlogRateDAO implements IBlogRateDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public BlogRateEntity getByBlogIdAndRateID(String blogId, String rateId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        BlogRateEntity result = null;

        String sql = "SELECT blog_rate.id, amount " +
                "FROM blog_rate " +
                "INNER JOIN blog ON blog_rate.blog_history_id = blog.blog_history_id " +
                "WHERE blog.id = ? AND rate_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);
            stm.setString(2, rateId);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                int amount = resultSet.getInt("amount");

                result = new BlogRateEntity(id, blogId, rateId, amount);
            }
        }

        return result;
    }

    @Override
    public List<BlogRateEntity> getByBlogId(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogRateEntity> result = null;

        String sql = "SELECT blog_rate.id, rate_id, amount " +
                "FROM blog_rate " +
                "INNER JOIN blog ON blog_rate.blog_history_id = blog.blog_history_id " +
                "WHERE blog.id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String rateId = resultSet.getString("rate_id");
                int amount = resultSet.getInt("amount");

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new BlogRateEntity(id, blogId, rateId, amount));
            }
        }

        return result;
    }

    @Override
    public boolean decreaseAmount(String blogId, String rateId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE blog_rate " +
                "SET amount = amount - 1 " +
                "WHERE blog_history_id = (SELECT blog.blog_history_id FROM blog WHERE blog.id = ?) " +
                "AND  rate_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);
            stm.setString(2, rateId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean insertByBlogIdAndRateID(String blogId, String rateId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO blog_rate (blog_history_id, rate_id, amount) " +
                "VALUES ((SELECT blog.blog_history_id FROM blog WHERE id = ?), ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);
            stm.setString(2, rateId);
            stm.setInt(3, 1);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean increaseAmount(String blogId, String rateId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE blog_rate " +
                "SET amount = amount + 1 " +
                "WHERE blog_history_id = (SELECT blog.blog_history_id FROM blog WHERE blog.id = ?) " +
                "AND  rate_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);
            stm.setString(2, rateId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }
}
