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

        String sql = "SELECT name "
                + "FROM category "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getNString(1);
                result = new CategoryEntity(id, name);
            }
        }

        return result;
    }

}
