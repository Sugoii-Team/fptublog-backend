package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.*;
import com.dsc.fptublog.service.interfaces.IAuthService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;

@Service
@RequestScoped
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
            student.setRole(Role.STUDENT);
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
        if (lecturer != null) {
            lecturer.setRole(Role.LECTURER);
            return lecturer;
        }
        return null;
    }

    @Override
    public AccountEntity createNewAccount(String email, String name, String avatarUrl) throws SQLException {
        AccountEntity result = null;
        try {
            connectionWrapper.beginTransaction();

            // create new Account
            AccountStatusEntity accountStatus = accountStatusDAO.getByName("activated");
            AccountEntity newAccount = accountDAO.createForNewEmail(email,
                    name.substring(0, Math.min(10, name.length())),
                    avatarUrl,
                    accountStatus.getId());

            if (newAccount != null) {
                // determine account is student or lecturer
                if (email.matches("^.*[0-9]{6}@fpt.edu.vn$")) {
                    // Create student account
                    result = createStudent(newAccount);
                } else {
                    // create lecturer account
                    result = createLecturer(newAccount);
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

    private StudentEntity createStudent(AccountEntity newAccount) throws SQLException {
        // Get school year from email string
        String email = newAccount.getEmail();
        int index = email.indexOf("@fpt.edu.vn") - 6;
        short schoolYear = Short.parseShort(email.substring(index, index+2));

        // Set default Major is Software Engineering because Major is not null
        // User can change it later
        MajorEntity major = majorDAO.getByName("Software Engineering");

        StudentEntity newStudent = studentDAO.insertByAccountIdAndMajorIdAndSchoolYear(
                newAccount.getId(), major.getId(), schoolYear);
        newStudent.setAccountInfo(newAccount);
        newStudent.setRole(Role.STUDENT);

        return newStudent;
    }
    
    private LecturerEntity createLecturer(AccountEntity newAccount) throws SQLException {
        LecturerEntity newLecturer = lecturerDAO.insertById(newAccount.getId());
        newLecturer.setAccountInfo(newAccount);
        newLecturer.setRole(Role.LECTURER);
        return newLecturer;
    }
}
