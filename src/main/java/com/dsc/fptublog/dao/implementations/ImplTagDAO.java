package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ITagDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
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
public class ImplTagDAO implements ITagDAO {
    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public TagEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        TagEntity result = null;

        String sql = "SELECT name "
                + "FROM tag "
                + "Where id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getNString(1);
                result = new TagEntity(id, name);
            }
        }

        return result;
    }

    @Override
    public List<TagEntity> getByIdList(List<String> idList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<TagEntity> result = null;

        String sql = "SELECT name " +
                "FROM tag " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (String id : idList) {
                stm.setString(1, id);

                ResultSet resultSet = stm.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getNString(1);

                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(new TagEntity(id, name));
                }
            }
        }

        return result;
    }

    @Override
    public List<TagEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<TagEntity> result = null;

        String sql = "SELECT id, name " +
                "FROM tag ";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new TagEntity(id, name));
            }
        }

        return result;
    }

    @Override
    public List<TagEntity> getTopTags(int limit, int offset) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<TagEntity> result = null;

        String sql = "SELECT tag.id, tag.name " +
                "FROM tag " +
                "INNER JOIN blog_tag bt on tag.id = bt.tag_id " +
                "GROUP BY tag.id, tag.name " +
                "ORDER BY COUNT(bt.blog_id) DESC " +
                "OFFSET ? ROW FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, offset);
            stm.setInt(2, limit);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new TagEntity(id, name));
            }
        }

        return result;
    }

    @Override
    public boolean deleteById(String deletedTagId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE "
                + "FROM tag "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, deletedTagId);

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }

        return false;

    }

    @Override
    public boolean updateByTag(TagEntity updatedTag) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE tag "
                + "SET name = ? "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, updatedTag.getName());

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public TagEntity insertByTag(TagEntity tag) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "INSERT INTO tag (name) " +
                "OUTPUT inserted.id " +
                "VALUES (?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setNString(1, tag.getName());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                tag.setId(resultSet.getString(1));
                return tag;
            }
        }

        return null;
    }

    @Override
    public List<TagEntity> insertIfNotExistedByTagList(List<TagEntity> tagList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql =
                "IF EXISTS (SELECT id FROM tag WHERE name = ?) " +
                        "SELECT id FROM tag WHERE name = ? " +
                        "ELSE " +
                        "INSERT INTO tag (name) " +
                        "OUTPUT inserted.id " +
                        "VALUES (?)";

        try (PreparedStatement stm = connection.prepareCall(sql)) {
            for (var tag : tagList) {
                stm.setNString(1, tag.getName());
                stm.setNString(2, tag.getName());
                stm.setNString(3, tag.getName());

                ResultSet resultSet = stm.executeQuery();
                if (resultSet.next()) {
                    tag.setId(resultSet.getString(1));
                } else {
                    return null;
                }
            }
        }
        return tagList;
    }
}
