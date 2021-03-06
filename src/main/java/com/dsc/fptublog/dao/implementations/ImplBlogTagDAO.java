package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogTagDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogTagEntity;
import com.dsc.fptublog.entity.TagEntity;
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
public class ImplBlogTagDAO implements IBlogTagDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<BlogTagEntity> getByBlogId(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogTagEntity> result = null;

        String sql = "SELECT id, tag_id " +
                "FROM blog_tag " +
                "WHERE blog_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String tagId = resultSet.getString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new BlogTagEntity(id, blogId, tagId));
            }
        }

        return result;
    }

    @Override
    public boolean createByBlogIdAndTagList(String blogId, List<TagEntity> tagList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO blog_tag (blog_id, tag_id) " +
                "VALUES (?, ?)";

        int effectedRow = 0;

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (TagEntity tag : tagList) {
                stm.setString(1, blogId);
                stm.setString(2, tag.getId());

                effectedRow += stm.executeUpdate();
            }
        }

        return effectedRow == tagList.size();
    }

    @Override
    public boolean deleteByBlogId(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE FROM blog_tag " +
                "WHERE blog_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }
}
