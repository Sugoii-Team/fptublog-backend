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
        AccountStatusEntity result = null;
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT name "
                    + "FROM account_status "
                    + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
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
        AccountStatusEntity result = null;
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT id "
                    + "FROM account_status "
                    + "WHERE name = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, name);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                result = new AccountStatusEntity(id, name);
            }
        }
        return result;
    }

    @Override
    public boolean updateByAccountStatus(AccountStatusEntity accountStatus) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "UPDATE account_status " +
                    "SET name = ? " +
                    "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, accountStatus.getName());
            stm.setString(2, accountStatus.getId());

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }
}