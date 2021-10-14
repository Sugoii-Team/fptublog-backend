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

        String sql = "INSERT INTO blog_history (created_datetime, views) " +
                "OUTPUT inserted.id " +
                "VALUES (?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, blogHistory.getCreatedDatetime());
            stm.setInt(2, blogHistory.getViews());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                blogHistory.setId(id);

                return blogHistory;
            }
        }

        return null;
    }
}
