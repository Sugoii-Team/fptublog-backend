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
        if (connection != null) {
            String sql = "SELECT name "
                    + "FROM category "
                    + "WHERE id = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, id);
            ResultSet result = stm.executeQuery();
            if (result.next()) {
                String name = result.getNString("name");
                CategoryEntity category = new CategoryEntity(id, name);
                return category;
            }
        }

        return null;
    }

}
