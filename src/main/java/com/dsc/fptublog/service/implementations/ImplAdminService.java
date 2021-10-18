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
        try {
            connectionWrapper.beginTransaction();
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
    public void deleteAccount(String accountId) throws SQLException {
        try {
            connectionWrapper.beginTransaction();
            AccountEntity deleteAccount = accountDAO.getById(accountId);
            AccountStatusEntity deleteStatus = accountStatusDAO.getByName("deleted");
            deleteAccount.setStatusId(deleteStatus.getId());
            boolean result = accountDAO.deleteAccount(deleteAccount);
            if (result){
                connectionWrapper.commit();
            }
        }catch (SQLException ex){
                connectionWrapper.rollback();
                throw ex;
        }finally {
            connectionWrapper.close();
        }
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

    @Override
    public boolean banAccount(AccountEntity account) throws SQLException {
        boolean result = false;
        try{
            connectionWrapper.beginTransaction();
            //set ban status Id for account
            AccountStatusEntity banStatus = accountStatusDAO.getByName("banned");
            account.setStatusId(banStatus.getId());
            result = accountDAO.updateByAccount(account);
            if(result){
                connectionWrapper.commit();
                return result;
            }
        }catch (SQLException ex){
            connectionWrapper.rollback();
            throw ex;
        }finally {
            connectionWrapper.close();
        }
        return result;
    }


}
