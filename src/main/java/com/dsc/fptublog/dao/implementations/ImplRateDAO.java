package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IRateDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.RateEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequestScoped
public class ImplRateDAO implements IRateDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public RateEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        RateEntity result = null;

        String sql = "SELECT star " +
                "FROM rate " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String star = resultSet.getString(1);

                result = new RateEntity(id, star);
            }
        }

        return result;
    }

    @Override
    public RateEntity getByName(String star) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        RateEntity result = null;

        String sql = "SELECT id " +
                "FROM rate " +
                "WHERE star = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, star);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString(1);

                result = new RateEntity(id, star);
            }
        }

        return result;
    }
}
