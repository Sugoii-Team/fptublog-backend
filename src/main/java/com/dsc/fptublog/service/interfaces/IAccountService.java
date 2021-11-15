package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.BannedInfoEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IAccountService {

    AccountEntity getById(String id) throws SQLException;

    List<AccountEntity> getTopAccount(int limit, int page) throws SQLException;

    boolean updateByAccount(AccountEntity updatedAccount) throws SQLException;

    List<AccountEntity> getBannedAccounts() throws SQLException;

    BannedInfoEntity getBannedInfoByAccountId(String accountId) throws SQLException;

}
