package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IAccountStatusDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerDAO;
import com.dsc.fptublog.dao.interfaces.IMajorDAO;
import com.dsc.fptublog.dao.interfaces.IStudentDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AccountStatusEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.entity.MajorEntity;
import com.dsc.fptublog.entity.StudentEntity;
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
    public AccountEntity getLogin(String email) throws Exception {
        AccountEntity result = null;

        try {
            connectionWrapper.beginTransaction();

            AccountEntity account = accountDAO.getByEmail(email);
            if (account == null) {
                account = accountDAO.getByAlternativeEmail(email);
            }

            if (account != null) {
                String bannedStatusId = accountStatusDAO.getByName("banned").getId();
                if (bannedStatusId.equals(account.getStatusId())) {
                    throw new Exception("{\"id\":\"" + account.getId() + "\"}");
                }

                result = studentDAO.getByAccount(account);
                if (result != null) {
                    result.setRole(Role.STUDENT);
                    return result;
                }

                result = lecturerDAO.getByAccount(account);
                if (result != null) {
                    result.setRole(Role.LECTURER);
                    return result;
                }
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public AccountEntity createNewAccount(String email, String name, String avatarUrl) throws SQLException {
        AccountEntity result = null;
        try {
            connectionWrapper.beginTransaction();

            // create new Account
            if (!email.matches("^.*@fpt.edu.vn$")) {
                return null;
            }

            AccountStatusEntity accountStatus = accountStatusDAO.getByName("activated");
            AccountEntity newAccount = null;
            if (email.matches("^.*[0-9]{6}@fpt.edu.vn$")) {
                newAccount = accountDAO.createForNewEmail(email,
                        name.substring(0, Math.min(10, name.length())),
                        avatarUrl,
                        accountStatus.getId(), Role.STUDENT);
                if (newAccount != null) {
                    // Create student account
                    result = createStudent(newAccount);
                }
            } else {
                newAccount = accountDAO.createForNewEmail(email,
                        name.substring(0, Math.min(10, name.length())),
                        avatarUrl,
                        accountStatus.getId(), Role.LECTURER);
                if (newAccount != null) {
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
        short schoolYear = Short.parseShort(email.substring(index, index + 2));

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
