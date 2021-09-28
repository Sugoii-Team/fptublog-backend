package com.dsc.fptublog.service;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.entity.StudentEntity;
import lombok.extern.log4j.Log4j;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;

@Log4j
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

    @Override
    public AccountEntity getAccountByEmail(String email) throws SQLException {
        // find account by email from DB
        AccountEntity account = null;
        try {
            connectionWrapper.beginTransaction();
            account = accountDAO.getByEmail(email);
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        if (account == null) {
            return account;
        }

        // check account is Student
        StudentEntity student = null;
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
        LecturerEntity lecturer = null;
        try {
            connectionWrapper.beginTransaction();
            lecturer = lecturerDAO.getByAccount(account);
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        if (lecturer != null) {
            return lecturer;
        }

        return null;
    }

    @Override
    public AccountEntity createNewAccount(String email, String name, String avatarUrl) {
        return null;
    }
}
