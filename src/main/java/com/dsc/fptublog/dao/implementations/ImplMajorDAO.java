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
import java.util.ArrayList;
import java.util.List;

@Service
@RequestScoped
public class ImplMajorDAO implements IMajorDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public MajorEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        MajorEntity result = null;
        String sql = "SELECT name " +
                "FROM major " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getNString(1);
                result = new MajorEntity(id, name);
            }
        }

        return result;
    }

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

    @Override
    public List<MajorEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<MajorEntity> result = null;

        String sql = "SELECT id, name " +
                "FROM major";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new MajorEntity(id, name));
            }
        }

        return result;
    }
}
