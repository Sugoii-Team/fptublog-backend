package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.*;
import com.dsc.fptublog.service.interfaces.IAuthService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;

@Service
public class ImplAuthService implements IAuthService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IStudentDAO studentDAO;

    @Inject
    private ILecturerDAO lecturerDAO;

    @Inject
    private IAccountStatusDAO accountStatusDAO;

    @Inject
    private IMajorDAO majorDAO;

    @Override
    public AccountEntity getAccountByEmail(String email) throws SQLException {
        // find account by email from DB
        AccountEntity account;
        try {
            connectionWrapper.beginTransaction();

            account = accountDAO.getByEmail(email);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        if (account == null) {
            return null;
        }

        // check account is Student
        StudentEntity student;
        try {
            connectionWrapper.beginTransaction();

            student = studentDAO.getByAccount(account);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        if (student != null) {
            return student;
        }

        // check account is Lecturer
        LecturerEntity lecturer;
        try {
            connectionWrapper.beginTransaction();

            lecturer = lecturerDAO.getByAccount(account);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return lecturer;
    }

    @Override
    public AccountEntity createNewAccount(String email, String name, String avatarUrl) throws SQLException {
        AccountEntity result;

        // create new account with default role is Student
        try {
            connectionWrapper.beginTransaction();

            // create new Account
            AccountStatusEntity accountStatus = accountStatusDAO.getByName("activated");
            AccountEntity newAccount = accountDAO.createForNewEmail(email,
                    name.substring(0, Math.min(10, name.length())),
                    avatarUrl,
                    accountStatus.getId());
            if (newAccount == null) {
                return null;
            }

            // create Student from newAccount with default Major is Software Engineering
            MajorEntity major = majorDAO.getByName("Software Engineering");
            result = studentDAO.createFromAccount(newAccount, major.getId());

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
