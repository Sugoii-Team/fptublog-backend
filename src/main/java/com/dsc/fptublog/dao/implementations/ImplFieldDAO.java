package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IFieldDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.FieldEntity;
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
public class ImplFieldDAO implements IFieldDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<FieldEntity> getByFieldIdList(List<String> fieldIdList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<FieldEntity> result = null;

        String sql = "SELECT name " +
                "FROM field " +
                "WHERE id = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (var fieldId : fieldIdList) {
                stm.setString(1, fieldId);

                ResultSet resultSet = stm.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getNString(1);
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(FieldEntity.builder().id(fieldId).name(name).build());
                }
            }
        }

        return result;
    }

    @Override
    public List<FieldEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<FieldEntity> result = null;

        String sql = "SELECT id, name " +
                "FROM field " +
                "WHERE status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(FieldEntity.builder().id(id).name(name).build());
            }
        }

        return result;
    }

    @Override
    public FieldEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT name " +
                "FROM field " +
                "WHERE id = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getNString(1);

                return FieldEntity.builder().id(id).name(name).build();
            }
        }

        return null;
    }

    @Override
    public List<FieldEntity> getTopFields() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<FieldEntity> result = null;

        String sql = "SELECT field.id, field.name, COUNT(b.id) AS blog_number " +
                "FROM field " +
                "INNER JOIN category ON field.id = category.field_id " +
                "LEFT JOIN " +
                "     ( " +
                "         SELECT blog.id, category_id " +
                "         FROM blog " +
                "         INNER JOIN blog_status status ON blog.status_id = status.id " +
                "         INNER JOIN blog_history history on history.id = blog.blog_history_id " +
                "         WHERE status.name = 'approved' OR status.name = 'pending deleted' " +
                "     ) AS b " +
                "ON category.id = b.category_id " +
                "WHERE field.status_id = (SELECT id FROM field_category_status WHERE name = 'active') " +
                "GROUP BY field.id, field.name " +
                "ORDER BY blog_number DESC";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(FieldEntity.builder().id(id).name(name).build());
            }
        }

        return result;
    }

    @Override
    public boolean updateField(FieldEntity updateField) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        int effectRow;
        if (connection == null) {
            return false;
        }
        String sql = "UPDATE field " +
                "SET name = ISNULL(?, name), status_id = ISNULL(?, status_id) " +
                "WHERE id = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, updateField.getName());
            stm.setString(2, updateField.getStatusId());
            stm.setString(3, updateField.getId());
            effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FieldEntity createField(FieldEntity newField) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        ResultSet result = null;
        if (connection == null) {
            return null;
        }
        String sql = "INSERT INTO field (name, status_id) "
                + "OUTPUT inserted.id "
                + "VALUES (?,?)";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, newField.getName());
            stm.setString(2, newField.getStatusId());
            result = stm.executeQuery();
            if (result.next()) {
                String fieldId = result.getString(1);
                newField.setId(fieldId);
                return newField;
            }
        }
        return null;
    }

    @Override
    public FieldEntity getByName(String name) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT id " +
                "FROM field " +
                "WHERE name = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getNString(1);

                return FieldEntity.builder().id(id).name(name).build();
            }
        }

        return null;
    }
}
