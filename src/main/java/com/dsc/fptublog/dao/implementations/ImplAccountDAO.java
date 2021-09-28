package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IAccountStatusDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AccountStatusEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import java.sql.*;

import javax.inject.Inject;

@Service
@RequestScoped
public class ImplAccountDAO implements IAccountDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountStatusDAO accountStatusDAO;

    @Override
    public AccountEntity getById(String id) throws SQLException {
        AccountEntity result = null;
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT email, alternative_email, firstname, lastname, password, avatar_url, description, status_id "
                    + "FROM account "
                    + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String alternativeEmail = resultSet.getString("alternative_email");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                String avatarUrl = resultSet.getString("avatar_url");
                String description = resultSet.getString("description");
                String statusId = resultSet.getString("status_id");
                AccountStatusEntity accountStatus = accountStatusDAO.getById(statusId);
                result = new AccountEntity(id, email, alternativeEmail, firstName, lastName,
                        password, avatarUrl, description, accountStatus);
            }
        }
        return result;
    }

    @Override
    public AccountEntity getByEmail(String email) throws SQLException {
        AccountEntity result = null;
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT id, alternative_email, firstname, lastname, password, avatar_url, description, status_id " +
                    "FROM account " +
                    "WHERE email = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, email);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String alternativeEmail = resultSet.getString("alternative_email");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                String avatarUrl = resultSet.getString("avatar_url");
                String description = resultSet.getString("description");
                String statusId = resultSet.getString("status_id");

                AccountStatusEntity accountStatus = accountStatusDAO.getById(statusId);

                result = new AccountEntity(id, email, alternativeEmail, firstName, lastName,
                        password, avatarUrl, description, accountStatus);
            }
        }
        return result;
    }

    @Override
    public boolean deleteById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "DELETE "
                    + "FROM account "
                    + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, id);

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateByAccount(AccountEntity updatedAccount) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "UPDATE account "
                    + "SET alternative_email = ?, firstname = ?, lastname = ?, password = ?, avatar_url = ?, description = ?, status_id = ? "
                    + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, updatedAccount.getAlternativeEmail());
            stm.setString(2, updatedAccount.getFirstName());
            stm.setString(3, updatedAccount.getLastName());
            stm.setString(4, updatedAccount.getPassword());
            stm.setString(5, updatedAccount.getAvatarUrl());
            stm.setString(6, updatedAccount.getDescription());
            stm.setString(7, updatedAccount.getStatus().getId());
            stm.setString(8, updatedAccount.getId());

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AccountEntity createByAccount(AccountEntity account) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "INSERT INTO account (email, alternative_email, firstname, lastname, password, avatar_url, description, status_id) " +
                    "OUTPUT inserted.id " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, account.getEmail());
            stm.setString(2, account.getAlternativeEmail());
            stm.setString(3, account.getFirstName());
            stm.setString(4, account.getLastName());
            stm.setString(5, account.getPassword());
            stm.setString(6, account.getAvatarUrl());
            stm.setString(7, account.getDescription());
            stm.setString(8, account.getStatus().getId());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                account.setId(resultSet.getString(1));
                return account;
            }
        }
        return null;
    }
}
