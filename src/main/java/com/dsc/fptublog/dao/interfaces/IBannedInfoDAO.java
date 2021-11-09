package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BannedInfoEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IBannedInfoDAO {

    boolean insertByAccountIdAndMessage(String accountId, String message) throws SQLException;

    boolean deleteByAccountId(String accountId) throws SQLException;

    BannedInfoEntity getByAccountId(String accountId) throws SQLException;
}
