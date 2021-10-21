package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAwardDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AwardEntity;
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
public class ImplAwardDAO implements IAwardDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<AwardEntity> getAllAwards() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<AwardEntity> result = null;

        String sql = "SELECT id, name, icon_url, point " +
                "FROM award ";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);
                String iconUrl = resultSet.getString(3);
                int point = resultSet.getInt(4);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new AwardEntity(id, name, iconUrl, point));
            }
        }

        return result;
    }

    @Override
    public AwardEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        AwardEntity result = null;

        String sql = "SELECT name, icon_url, point " +
                "FROM award " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getNString(1);
                String iconUrl = resultSet.getString(2);
                int point = resultSet.getInt(3);

                result = new AwardEntity(id, name, iconUrl, point);
            }
        }

        return result;
    }
}
