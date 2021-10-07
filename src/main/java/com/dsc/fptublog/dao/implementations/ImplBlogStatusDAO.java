package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogStatusDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogStatusEntity;
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
public class ImplBlogStatusDAO implements IBlogStatusDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public BlogStatusEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        BlogStatusEntity result = null;

        String sql = "SELECT name "
                + "FROM blog_status "
                + "Where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                result = new BlogStatusEntity(id, name);
            }
        }

        return result;
    }

    @Override
    public BlogStatusEntity getByName(String name) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        BlogStatusEntity result = null;

        String sql = "SELECT id "
                + "FROM blog_status "
                + "Where name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString(1);
                result = new BlogStatusEntity(id, name);
            }
        }

        return result;
    }

    @Override
    public boolean deleteById(String deletedBlogStatusId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE "
                + "FROM blog_status "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, deletedBlogStatusId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean updateByBlogStatus(BlogStatusEntity updatedBlogStatus) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE blog_status "
                + "SET name = ? "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, updatedBlogStatus.getName());

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlogStatusEntity insertByBlogStatus(BlogStatusEntity blogStatus) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "INSERT INTO blog_status (name) " +
                "OUTPUT inserted.id " +
                "VALUES (?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogStatus.getName());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                blogStatus.setId(resultSet.getString(1));
                return blogStatus;
            }
        }

        return null;
    }

    @Override
    public List<BlogStatusEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogStatusEntity> result = null;

        String sql = "SELECT id, name " +
                "FROM blog_status";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new BlogStatusEntity(id, name));
            }
        }

        return result;
    }
}

