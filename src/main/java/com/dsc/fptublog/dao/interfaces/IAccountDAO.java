package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IAccountDAO {

    AccountEntity getByEmail(String email) throws SQLException;

    AccountEntity getByAlternativeEmail(String email) throws SQLException;

    AccountEntity createForNewEmail(String email, String name, String avatarUrl, String statusId, String role) throws SQLException;

    AccountEntity getById(String id) throws SQLException;

    List<AccountEntity> getTopAccounts(int limit, int offset) throws SQLException;

    boolean updateByAccount(AccountEntity updatedAccount) throws SQLException;

    List<AccountEntity> getAllAccounts() throws SQLException;

    boolean deleteAccount(AccountEntity deletedAccount) throws SQLException;

    List<AccountEntity> getAllBannedAccounts() throws SQLException;

    boolean decreaseNumberOfBlog(String id) throws SQLException;

    boolean increaseNumberOfBlog(String id) throws SQLException;

    AccountEntity getAdminAccount() throws SQLException;

    boolean updateNumberOfBlogAndAvgRate(String id) throws SQLException;
}
