package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAccountService {

    AccountEntity getById(String id) throws SQLException;

    boolean updateByAccount(AccountEntity updatedAccount) throws SQLException;

}
