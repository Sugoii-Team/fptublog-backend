package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAuthService {

    public AccountEntity getAccount(String email, String name, String avatarUrl) throws SQLException;
}
