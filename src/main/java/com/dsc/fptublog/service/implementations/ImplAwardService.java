package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IAwardDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerStudentAwardDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AwardEntity;
import com.dsc.fptublog.entity.LecturerStudentAwardEntity;
import com.dsc.fptublog.service.interfaces.IAwardService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplAwardService implements IAwardService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAwardDAO awardDAO;

    @Inject
    ILecturerStudentAwardDAO lecturerStudentAwardDAO;

    @Override
    public List<AwardEntity> getAwards() throws SQLException {
        List<AwardEntity> result = null;

        try {
            connectionWrapper.beginTransaction();

            result = awardDAO.getAllAwards();
            if (result == null) {
                result = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public AwardEntity getAward(String id) throws SQLException {
        AwardEntity result = null;

        try {
            connectionWrapper.beginTransaction();

            result = awardDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public boolean giveAward(LecturerStudentAwardEntity lecturerStudentAward) throws SQLException {
        boolean result = false;

        try {
            connectionWrapper.beginTransaction();

            result = lecturerStudentAwardDAO.insertByLecturerStudentAward(lecturerStudentAward);

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
    public List<LecturerStudentAwardEntity> getAllAwardOfStudent(String studentId) throws SQLException {
        List<LecturerStudentAwardEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = lecturerStudentAwardDAO.getByStudentId(studentId);
            if (result == null) {
                result = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public LecturerStudentAwardEntity deleteAwardOfStudent(String id, String lecturerId, String studentId)
            throws SQLException {
        LecturerStudentAwardEntity result = null;

        try {
            connectionWrapper.beginTransaction();

            LecturerStudentAwardEntity oldAward = lecturerStudentAwardDAO.getById(id);
            if (oldAward == null) {
                return null;
            }

            if (!oldAward.getLecturerId().equals(lecturerId) || !oldAward.getStudentId().equals(studentId)) {
                return null;
            }

            if (lecturerStudentAwardDAO.deleteById(id)) {
                result = oldAward;
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

}
