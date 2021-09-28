package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IAccountStatusDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import java.sql.*;

import javax.inject.Inject;

@Service
@RequestScoped
public class ImplAccountDAO implements IAccountDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public AccountEntity getById(String id) throws SQLException {
        return null;
    }

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
    public boolean deleteById(String id) throws SQLException {
        return false;
    }

    @Override
    public boolean updateByAccount(AccountEntity updatedAccount) throws SQLException {
        return false;
    }

    @Override
    public AccountEntity createByAccount(AccountEntity account) throws SQLException {
        return null;
    }
}
