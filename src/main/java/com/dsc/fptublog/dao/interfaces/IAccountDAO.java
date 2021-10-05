package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAccountDAO {

    AccountEntity getByEmail(String email) throws SQLException;

    AccountEntity createForNewEmail(String email, String name, String avatarUrl, String statusId) throws SQLException;

    AccountEntity getById(String id) throws SQLException;

    public boolean updateByAccount(AccountEntity updatedAccount) throws SQLException;

}
