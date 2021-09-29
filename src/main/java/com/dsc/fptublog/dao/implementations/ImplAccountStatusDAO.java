package com.dsc.fptublog.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.dsc.fptublog.dao.interfaces.IAccountStatusDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountStatusEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Service
@RequestScoped
public class ImplAccountStatusDAO implements IAccountStatusDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public AccountStatusEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            return null;
        }

        AccountStatusEntity result = null;

        String sql = "SELECT name "
                + "FROM account_status "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                result = new AccountStatusEntity(id, name);
            }
        }

        return result;
    }

    @Override
    public AccountStatusEntity getByName(String name) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            return null;
        }

        AccountStatusEntity result = null;

        String sql = "SELECT id "
                + "FROM account_status "
                + "WHERE name = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                result = new AccountStatusEntity(id, name);
            }
        }

        return result;
    }
}