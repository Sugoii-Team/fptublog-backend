package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IBannedInfoDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.BannedInfoEntity;
import com.dsc.fptublog.service.interfaces.IAccountService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplAccountService implements IAccountService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IBannedInfoDAO bannedInfoDAO;

    @Override
    public AccountEntity getById(String id) throws SQLException {
        AccountEntity account;
        try {
            connectionWrapper.beginTransaction();

            account = accountDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return account;
    }

    @Override
    public boolean updateByAccount(AccountEntity account) throws SQLException {
        boolean result;

        try {
            connectionWrapper.beginTransaction();

            result = accountDAO.updateByAccount(account);

            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public List<AccountEntity> getBannedAccounts() throws SQLException {
        List<AccountEntity> result = null;

        try {
            connectionWrapper.beginTransaction();

            result = accountDAO.getAllBannedAccounts();

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public BannedInfoEntity getBannedInfoByAccountId(String accountId) throws SQLException {
        BannedInfoEntity result;

        try {
            connectionWrapper.beginTransaction();

            result = bannedInfoDAO.getByAccountId(accountId);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }
}
