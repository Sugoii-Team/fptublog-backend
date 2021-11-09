package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IAccountStatusDAO;
import com.dsc.fptublog.dao.interfaces.IBannedInfoDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AccountStatusEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.service.interfaces.ILecturerService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplLecturerService implements ILecturerService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private ILecturerDAO lecturerDAO;

    @Inject
    private IAccountStatusDAO accountStatusDAO;

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IBannedInfoDAO bannedInfoDAO;

    @Override
    public List<LecturerEntity> getAllLecturers() throws SQLException {
        List<LecturerEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = lecturerDAO.getAll();

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
    public LecturerEntity getLecturer(String id) throws SQLException {
        LecturerEntity result;

        try {
            connectionWrapper.beginTransaction();

            result = lecturerDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public boolean banStudent(String studentId, String message) throws SQLException {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();
            // Set ban status Id for student account
            AccountStatusEntity banStatus = accountStatusDAO.getByName("banned");
            AccountEntity bannedStudentAccount = accountDAO.getById(studentId);
            if (bannedStudentAccount == null) {
                return false;
            }
            bannedStudentAccount.setStatusId(banStatus.getId());
            result = accountDAO.updateByAccount(bannedStudentAccount);

            // add Banned message
            result &= bannedInfoDAO.insertByAccountIdAndMessage(studentId, message);

            if (result) {
                connectionWrapper.commit();
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public boolean unbanStudent(String studentId) throws SQLException {
        boolean result;
        try {
            connectionWrapper.beginTransaction();
            AccountEntity unbannedStudentAccount = accountDAO.getById(studentId);
            if (unbannedStudentAccount == null) {
                return false;
            } else {
                //set active status for student Id
                AccountStatusEntity activeStatus = accountStatusDAO.getByName("activated");
                unbannedStudentAccount.setStatusId(activeStatus.getId());
                result = accountDAO.updateByAccount(unbannedStudentAccount);

                // delete banned message
                result &= bannedInfoDAO.deleteByAccountId(studentId);

                if (result) {
                    connectionWrapper.commit();
                }
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }
}
