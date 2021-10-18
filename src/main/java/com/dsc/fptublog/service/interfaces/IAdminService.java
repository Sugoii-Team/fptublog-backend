package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AdminEntity;
import org.jvnet.hk2.annotations.Contract;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

@Contract
public interface IAdminService {
    List<AccountEntity> getAllAccounts() throws SQLException;

    AccountEntity updateAccount(AccountEntity account) throws SQLException;

    void deleteAccount(String accountId) throws SQLException;

    boolean getAuthentication(AdminEntity admin) throws SQLException, NoSuchAlgorithmException;

    boolean banAccount(AccountEntity account) throws SQLException;
}
