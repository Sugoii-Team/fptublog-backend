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


@Service
@RequestScoped
public class ImplBlogStatusDAO implements IBlogStatusDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;


    @Override
    public BlogStatusEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        PreparedStatement statement = null;
        ResultSet result = null;
        if (connection != null) {
            String sql = "SELECT name "
                    + "FROM blog_status "
                    + "Where id = ?";

            statement = connection.prepareStatement(sql);
            statement.setString(1, id);

            result = statement.executeQuery();
            if (result.next()) {
                String name = result.getString("name");
                BlogStatusEntity blogStatus = new BlogStatusEntity(id, name);
                return blogStatus;
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(String deletedBlogStatusId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "DELETE "
                    + "FROM blog_status "
                    + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, deletedBlogStatusId);

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean updateByBlogStatus(BlogStatusEntity updatedBlogStatus) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "UPDATE blog_status "
                    + "SET name = ? "
                    + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
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
        if (connection != null) {
            String sql = "INSERT INTO blog_status (name) " +
                    "OUTPUT inserted.id " +
                    "VALUES (?)";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, blogStatus.getName());


            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                blogStatus.setId(resultSet.getString(1));
                return blogStatus;
            }
        }

        return null;
    }
}

