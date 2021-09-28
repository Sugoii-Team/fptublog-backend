package com.dsc.fptublog.service;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAuthService {

    AccountEntity getAccountByEmail(String email) throws SQLException;

    AccountEntity createNewAccount(String email, String name, String avatarUrl);
}
