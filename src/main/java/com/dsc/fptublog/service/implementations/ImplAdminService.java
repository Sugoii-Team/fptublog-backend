package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.*;
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

    @Inject
    private IBlogDAO blogDAO;

    @Inject
    private IBlogStatusDAO blogStatusDAO;

    @Inject
    private ILecturerStudentAwardDAO lecturerStudentAwardDAO;

    @Inject
    private IStudentDAO studentDAO;

    @Inject
    private ILecturerDAO lecturerDAO;

    @Inject
    private ILecturerFieldDAO lecturerFieldDAO;

    @Inject
    private IMajorDAO majorDAO;

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
    public AccountEntity updateRole(AccountEntity account) throws SQLException {
        AccountEntity result = null;
        try {
            connectionWrapper.beginTransaction();

            // Update to Lecturer
            if ("LECTURER".equals(account.getRole())) {
                // delete all lecturer_student_award of this student
                if (lecturerStudentAwardDAO.deleteByStudentId(account.getId())) {
                    // delete the student
                    if (studentDAO.deleteStudentById(account.getId())) {
                        // create new Lecturer
                        AccountEntity newAccount = AccountEntity.builder()
                                .id(account.getId())
                                .role(account.getRole())
                                .build();
                        if (accountDAO.updateByAccount(newAccount)) {
                            result = lecturerDAO.insertById(account.getId());
                        }
                    }
                }
            }

            // Update to Student
            if ("STUDENT".equals(account.getRole())) {
                // delete all lecturer_student_award of this lecturer
                if (lecturerStudentAwardDAO.deleteByLecturerId(account.getId())) {
                    // delete lecturer fields
                    if (lecturerFieldDAO.deleteByLecturerId(account.getId())) {
                        // convert blogs reviewed by this lecturer to pending approved
                        if (blogDAO.deleteReviewerId(account.getId())) {
                            AccountEntity newAccount = AccountEntity.builder()
                                    .id(account.getId())
                                    .role(account.getRole())
                                    .build();
                            if (lecturerDAO.deleteById(account.getId())) {
                                if (accountDAO.updateByAccount(newAccount)) {
                                    String majorId = majorDAO.getByName("Software Engineering").getId();
                                    result = studentDAO.insertByAccountIdAndMajorIdAndSchoolYear(account.getId(),
                                            majorId, (short) 0);
                                }
                            }
                        }
                    }
                }
            }

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

    @Override
    public List<AccountEntity> getAllBannedAccounts() throws SQLException {
        List<AccountEntity> bannedAccounts;
        try{
            connectionWrapper.beginTransaction();
            bannedAccounts = accountDAO.getAllBannedAccounts();
            connectionWrapper.commit();
        }finally {
            connectionWrapper.close();
        }
        return bannedAccounts;
    }

    @Override
    public boolean deleteBlog(String id) throws SQLException {
        try{
            connectionWrapper.beginTransaction();
            BlogEntity deleteBlog = blogDAO.blogIdIsExistent(id); // check blog is exist
            BlogStatusEntity deleteStatus = blogStatusDAO.getByName("deleted");
            deleteBlog.setStatusId(deleteStatus.getId()); // set delete status id into blog
            boolean result = blogDAO.updateByBlog(deleteBlog); //update Blog's status to deleted
            if(result){
                connectionWrapper.commit();
                return true;
            }
        }catch (SQLException ex){
            connectionWrapper.rollback();
            throw ex;
        }finally {
            connectionWrapper.close();
        }
        return false;
    }


}
