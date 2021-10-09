package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IAccountStatusDAO;
import com.dsc.fptublog.dao.interfaces.IAdminDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AccountStatusEntity;
import com.dsc.fptublog.entity.AdminEntity;
import com.dsc.fptublog.service.interfaces.IAdminService;
import com.dsc.fptublog.util.SHA256Util;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

@Service
@RequestScoped
public class ImplAdminService implements IAdminService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IAccountStatusDAO accountStatusDAO;

    @Inject
    private IAdminDAO adminDAO;


    @Override
    public List<AccountEntity> getAllAccounts() throws SQLException {
        List<AccountEntity> accountList;
        try {
            connectionWrapper.beginTransaction();
            accountList = accountDAO.getAllAccounts();
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return accountList;
    }

    @Override
    public AccountEntity updateAccount(AccountEntity account) throws SQLException {
        AccountStatusEntity statusEntity;
        try {
            connectionWrapper.beginTransaction();
            statusEntity = accountStatusDAO.getByName("deleted");
            if (account.getStatusId().equals(statusEntity.getId())) {
                account.setStatusId(statusEntity.getId());
            }//end if account status is set to deleted
            boolean resultUpdate = accountDAO.updateByAccount(account);
            if (resultUpdate) {
                connectionWrapper.commit();
                return account;
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return null;
    }

    @Override
    public boolean getAuthentication(AdminEntity admin) throws SQLException, NoSuchAlgorithmException {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();

            admin.setPassword(SHA256Util.getEncryptedPassword(admin.getPassword()));
            result = adminDAO.checkAuthentication(admin);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }


}
