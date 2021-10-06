package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IAdminService {
    public List<AccountEntity> getAllAccounts() throws SQLException;

    public AccountEntity updateAccount(AccountEntity account) throws SQLException;


}
