package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAdminDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImplAdminDAO implements IAdminDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<AccountEntity> getAllAccounts() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        List<AccountEntity> accountList = null;
        if (connection == null) {
            return null;
        }
        String sql = "SELECT id, email, alternative_email, firstname, lastname, status_id "
                + "FROM account";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet result = stm.executeQuery();
            while (result.next()) {
                String id = result.getString(1);
                String email = result.getString(2);
                String alternativeEmail = result.getString(3);
                String firstName = result.getNString(4);
                String lastName = result.getNString(5);
                String statusId = result.getString(6);
                if (accountList == null) {
                    accountList = new ArrayList<>();
                }
                accountList.add(AccountEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstName)
                        .lastName(lastName)
                        .statusId(statusId).build());
            }
        }
        return accountList;
    }

    @Override
    public AccountEntity updateAccount(AccountEntity account) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if(connection == null){
            return null;
        }
        int effectRow;
        String sql ="UPDATE account "
                    +"SET alternative_email = ISNULL(?, alternative_email), firstname = ISNULL(?, firstname), lastname = ISNULL(?, lastname), password = ISNULL(?, password), avatar_url = ISNULL(?, avatar_url), description = ISNULL(?, description), status_id = ISNULL(?, status_id) "
                    +"WHERE id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1,account.getAlternativeEmail());
            stm.setNString(2,account.getFirstName());
            stm.setNString(3,account.getLastName());
            stm.setString(4,account.getPassword());
            stm.setString(5,account.getAvatarUrl());
            stm.setNString(6,account.getDescription());
            stm.setString(7,account.getStatusId());
            stm.setString(8,account.getId());
            effectRow = stm.executeUpdate();
            if(effectRow > 0){
                return account;
            }
        }
        return null;
    }


}
