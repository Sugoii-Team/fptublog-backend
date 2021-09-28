package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAccountDAO {

    public AccountEntity getById(String id) throws SQLException;

    public AccountEntity getByEmail(String email) throws SQLException;

    public boolean deleteById(String deletedAccountId) throws SQLException;

    public boolean updateByAccount(AccountEntity updatedAccount) throws SQLException;

    public AccountEntity createByAccount(AccountEntity account) throws SQLException;
}
