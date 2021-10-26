package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@Service
@RequestScoped
public class ImplAccountDAO implements IAccountDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public AccountEntity getByEmail(String email) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        AccountEntity result = null;

        String sql = "SELECT id, alternative_email, firstname, lastname, avatar_url, description, status_id " +
                "FROM account " +
                "WHERE email = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, email);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString(1);
                String alternativeEmail = resultSet.getString(2);
                String firstName = resultSet.getNString(3);
                String lastName = resultSet.getNString(4);
                String avatarUrl = resultSet.getString(5);
                String description = resultSet.getNString(6);
                String statusId = resultSet.getString(7);

                result = AccountEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarUrl(avatarUrl)
                        .description(description)
                        .statusId(statusId)
                        .build();
            }
        }

        return result;
    }

    @Override
    public AccountEntity createForNewEmail(String email, String name, String avatarUrl, String statusId, String role)
            throws SQLException {
        if (email == null || name == null) {
            return null;
        }

        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        AccountEntity result = null;

        String sql = "INSERT INTO account (email, alternative_email, firstname, lastname, avatar_url, status_id, role) " +
                "OUTPUT inserted.id " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, email);
            stm.setString(2, email); // because alternative_email is unique => can't be NULL is 2 different rows
            stm.setNString(3, name);
            stm.setNString(4, "");
            stm.setString(5, avatarUrl);
            stm.setString(6, statusId);
            stm.setString(7,role);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString(1);

                result = AccountEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(email)
                        .firstName(name)
                        .lastName("")
                        .avatarUrl(avatarUrl)
                        .statusId(statusId)
                        .role(role)
                        .build();
            }
        }

        return result;
    }

    @Override
    public AccountEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        AccountEntity result = null;

        String sql = "SELECT email, alternative_email, firstname, lastname, avatar_url, description, status_id " +
                "FROM account " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String email = resultSet.getString(1);
                String alternativeEmail = resultSet.getString(2);
                String firstName = resultSet.getNString(3);
                String lastName = resultSet.getNString(4);
                String avatarUrl = resultSet.getString(5);
                String description = resultSet.getNString(6);
                String statusId = resultSet.getString(7);

                result = AccountEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarUrl(avatarUrl)
                        .description(description)
                        .statusId(statusId)
                        .build();
            }
        }

        return result;
    }

    @Override
    public boolean updateByAccount(AccountEntity updatedAccount) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }
        String sql = "UPDATE account "
                + "SET alternative_email = ISNULL(?, alternative_email), firstname = ISNULL(?, firstname), lastname = ISNULL(?, lastname), avatar_url = ISNULL(?, avatar_url), description = ISNULL(?, description), status_id = ISNULL(?, status_id), role = ISNULL(?, role) "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, updatedAccount.getAlternativeEmail());
            stm.setString(2, updatedAccount.getFirstName());
            stm.setString(3, updatedAccount.getLastName());
            stm.setString(4, updatedAccount.getAvatarUrl());
            stm.setString(5, updatedAccount.getDescription());
            stm.setString(6, updatedAccount.getStatusId());
            stm.setString(7, updatedAccount.getRole());
            stm.setString(8, updatedAccount.getId());

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AccountEntity> getAllAccounts() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        List<AccountEntity> accountList = null;
        if (connection == null) {
            return null;
        }
        String sql = "SELECT account.id, email, alternative_email, firstname, lastname, status_id, role "
                + "FROM account "
                + "INNER JOIN account_status status ON account.status_id = status.id "
                + "WHERE status.name = 'activated'";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet result = stm.executeQuery();
            while (result.next()) {
                String id = result.getString(1);
                String email = result.getString(2);
                String alternativeEmail = result.getString(3);
                String firstName = result.getNString(4);
                String lastName = result.getNString(5);
                String statusId = result.getString(6);
                String role = result.getString(7);
                if (accountList == null) {
                    accountList = new ArrayList<>();
                }
                accountList.add(AccountEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstName)
                        .lastName(lastName)
                        .statusId(statusId)
                        .role(role).build());
            }
        }
        return accountList;
    }

    @Override
    public boolean deleteAccount(AccountEntity deletedAccount) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }
        String sql = "UPDATE account "
                + "SET status_id = ISNULL(?, status_id) "
                + "WHERE id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, deletedAccount.getStatusId());
            stm.setString(2, deletedAccount.getId());
            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AccountEntity> getAllBannedAccounts() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        List<AccountEntity> accountList = null;
        if (connection == null) {
            return null;
        }
        String sql = "SELECT account.id, email, alternative_email, firstname, lastname, status_id, role "
                + "FROM account "
                + "INNER JOIN account_status status ON account.status_id = status.id "
                + "WHERE status.name = 'banned'";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet result = stm.executeQuery();
            while (result.next()) {
                String id = result.getString(1);
                String email = result.getString(2);
                String alternativeEmail = result.getString(3);
                String firstName = result.getNString(4);
                String lastName = result.getNString(5);
                String statusId = result.getString(6);
                String role = result.getString(7);
                if (accountList == null) {
                    accountList = new ArrayList<>();
                }
                accountList.add(AccountEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstName)
                        .lastName(lastName)
                        .statusId(statusId)
                        .role(role).build());
            }
        }
        return accountList;
    }
}
