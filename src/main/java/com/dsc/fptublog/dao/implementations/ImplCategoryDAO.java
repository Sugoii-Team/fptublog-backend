package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ICategoryDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.CategoryEntity;
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
public class ImplCategoryDAO implements ICategoryDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public CategoryEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        CategoryEntity result = null;

        String sql = "SELECT name, field_id "
                + "FROM category "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getNString(1);
                String fieldId = resultSet.getString(2);
                result = new CategoryEntity(id, name, fieldId);
            }
        }

        return result;
    }

    @Override
    public List<CategoryEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<CategoryEntity> result = null;

        String sql = "SELECT id, name, field_id " +
                "FROM category";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);
                String fieldId = resultSet.getString(3);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new CategoryEntity(id, name, fieldId));
            }
        }

        return result;
    }

}
