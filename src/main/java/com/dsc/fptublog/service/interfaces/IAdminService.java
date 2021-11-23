package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AdminEntity;
import com.dsc.fptublog.entity.BlogEntity;
import org.jvnet.hk2.annotations.Contract;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

@Contract
public interface IAdminService {
    List<AccountEntity> getAllAccounts() throws SQLException;

    AccountEntity updateAccount(AccountEntity account) throws SQLException;

    AccountEntity updateRole(AccountEntity account) throws SQLException;

    void deleteAccount(String accountId) throws SQLException;

    boolean getAuthentication(AdminEntity admin) throws SQLException, NoSuchAlgorithmException;

    boolean banAccount(String accountId, String message) throws SQLException;

    List<AccountEntity> getAllBannedAccounts() throws SQLException;

    boolean deleteBlog(String id) throws SQLException;

    boolean unbanAccount(String accountId) throws SQLException;

    BlogEntity createBlog(BlogEntity newBlog) throws SQLException;

    BlogEntity updateBlog(BlogEntity updatedBlog) throws SQLException;

    List<BlogEntity> getAllBlogsOfAdmin(int limit, int page) throws SQLException;
}
