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
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (var fieldId : fieldIdList) {
                stm.setString(1, fieldId);

                ResultSet resultSet = stm.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getNString(1);
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(new FieldEntity(fieldId, name));
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
                "FROM field";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new FieldEntity(id, name));
            }
        }

        return result;
    }
}
