package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAccountDAO {

    public AccountEntity getByEmail(String email) throws SQLException;

    public AccountEntity createForNewEmail(String email, String name, String avatarUrl, String statusId) throws SQLException;
}
