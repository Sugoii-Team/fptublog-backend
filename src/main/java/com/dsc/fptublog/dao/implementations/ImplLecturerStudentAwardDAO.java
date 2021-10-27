package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ILecturerStudentAwardDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.LecturerStudentAwardEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequestScoped
public class ImplLecturerStudentAwardDAO implements ILecturerStudentAwardDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public boolean insertByLecturerStudentAward(LecturerStudentAwardEntity lecturerStudentAward) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO lecturer_student_award " +
                "(lecturer_id, student_id, award_id) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, lecturerStudentAward.getLecturerId());
            stm.setString(2, lecturerStudentAward.getStudentId());
            stm.setString(3, lecturerStudentAward.getAwardId());

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<LecturerStudentAwardEntity> getByStudentId(String studentId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<LecturerStudentAwardEntity> result = null;

        String sql = "SELECT id, lecturer_id, award_id " +
                "FROM lecturer_student_award " +
                "WHERE student_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, studentId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String lecturerId = resultSet.getString(2);
                String awardId = resultSet.getString(3);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new LecturerStudentAwardEntity(id, lecturerId, studentId, awardId));
            }
        }

        return result;
    }

    @Override
    public LecturerStudentAwardEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        LecturerStudentAwardEntity result = null;

        String sql = "SELECT lecturer_id, student_id, award_id " +
                "FROM lecturer_student_award " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String lecturerId = resultSet.getString(1);
                String studentId = resultSet.getString(2);
                String awardId = resultSet.getString(3);

                result = new LecturerStudentAwardEntity(id, lecturerId, studentId, awardId);
            }
        }

        return result;
    }

    @Override
    public boolean deleteById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE FROM lecturer_student_award " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteByStudentId(String studentId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE FROM lecturer_student_award " +
                "WHERE student_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, studentId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteByLecturerId(String lecturerId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE FROM lecturer_student_award " +
                "WHERE lecturer_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, lecturerId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }
}
