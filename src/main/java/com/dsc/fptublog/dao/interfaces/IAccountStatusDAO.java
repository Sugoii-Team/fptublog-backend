package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountStatusEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IAccountStatusDAO {
    
    public AccountStatusEntity getById(String statusId) throws SQLException;

    public AccountStatusEntity getByName(String name) throws SQLException;

}
