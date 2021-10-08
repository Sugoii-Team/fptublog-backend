package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.service.interfaces.IAccountService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;

@Service
@RequestScoped
public class ImplAccountService implements IAccountService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountDAO accountDAO;

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
}
