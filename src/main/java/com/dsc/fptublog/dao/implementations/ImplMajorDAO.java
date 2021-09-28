package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IMajorDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.MajorEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequestScoped
public class ImplMajorDAO implements IMajorDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public MajorEntity getByName(String name) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        MajorEntity result = null;
        String sql = "SELECT id " +
                "FROM major " +
                "WHERE name = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setNString(1, name);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                result = new MajorEntity(id, name);
            }
        }

        return result;
    }
}
