package com.dsc.fptublog.service.implementation;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.service.interfaces.IAuthService;
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
    private IMajorDAO majorDAO;

    @Inject
    private ILecturerDAO lecturerDAO;

    @Inject
    private IAccountStatusDAO accountStatusDAO;

    @Override
    public AccountEntity getAccount(String email, String name, String avatarUrl) throws SQLException {
        // Get account by email from DB
        AccountEntity account = null;
        connectionWrapper.beginTransaction();
        account = accountDAO.getByEmail(email);
        connectionWrapper.commit();
        connectionWrapper.close();

        if (account != null) {
            // If existed account, determine its role
            StudentEntity student = null;
            connectionWrapper.beginTransaction();
            student = studentDAO.getByAccount(account);
            connectionWrapper.commit();

            if (student != null) {
                return student;
            }

            LecturerEntity lecturer = null;
            connectionWrapper.beginTransaction();
            lecturer = lecturerDAO.getByAccount(account);
            connectionWrapper.commit();

            if (lecturer != null) {
                return lecturer;
            }
        } else {
            // If not existed account, create new registration for this email
            StudentEntity student = null;
            try {
                connectionWrapper.beginTransaction();
                student = StudentEntity.builder()
                        .email(email)
                        .firstName(name.substring(0, Math.min(name.length(), 10)))
                        .lastName("")
                        .status(accountStatusDAO.getByName("activated"))
                        .major(majorDAO.getByName("Software Engineering"))
                        .avatarUrl(avatarUrl)
                        .build();
                student = studentDAO.createByStudent(student);
                connectionWrapper.commit();
            } catch (SQLException ex) {
                connectionWrapper.rollback();
                throw ex;
            }finally {
                connectionWrapper.close();
            }
            return student;
        }

        return null;
    }
}
