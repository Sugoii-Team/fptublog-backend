package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IStudentDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.StudentEntity;
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
public class ImplStudentDAO implements IStudentDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<StudentEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<StudentEntity> result = null;

        String sql = "SELECT  account.id, email, alternative_email, firstname, lastname, avatar_url,  description, " +
                "status_id, role, school_year, major_id, experience_point " +
                "FROM account " +
                "INNER JOIN account_student AS student ON account.id = student.id " +
                "INNER JOIN account_status AS status on status.id = account.status_id " +
                "WHERE status.name = 'activated'";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String email = resultSet.getString(2);
                String alternativeEmail = resultSet.getString(3);
                String firstname = resultSet.getNString(4);
                String lastname = resultSet.getNString(5);
                String avatarUrl = resultSet.getString(6);
                String description = resultSet.getNString(7);
                String statusId = resultSet.getString(8);
                String role = resultSet.getString(9);
                short schoolYear = resultSet.getShort(10);
                String majorId = resultSet.getString(11);
                int experiencePoint = resultSet.getInt(12);

                StudentEntity student = StudentEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstname)
                        .lastName(lastname)
                        .avatarUrl(avatarUrl)
                        .description(description)
                        .statusId(statusId)
                        .role(role)
                        .schoolYear(schoolYear)
                        .majorId(majorId)
                        .experiencePoint(experiencePoint)
                        .build();

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(student);
            }
        }

        return result;
    }

    @Override
    public StudentEntity createFromAccount(AccountEntity account, String majorId) throws SQLException {
        if (account == null) {
            return null;
        }

        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        StudentEntity result = null;

        String sql = "INSERT INTO account_student (id, major_id) " +
                "VALUES (?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, account.getId());
            stm.setString(2, majorId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                result = StudentEntity.builder()
                        .id(account.getId())
                        .email(account.getEmail())
                        .alternativeEmail(account.getAlternativeEmail())
                        .firstName(account.getFirstName())
                        .lastName(account.getLastName())
                        .avatarUrl(account.getAvatarUrl())
                        .statusId(account.getStatusId())
                        .majorId(majorId)
                        .build();
            }
        }

        return result;
    }

    @Override
    public StudentEntity insertByAccountIdAndMajorIdAndSchoolYear(String accountId, String majorId, short schoolYear)
            throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        StudentEntity result = null;

        String sql = "INSERT INTO account_student (id, major_id, school_year) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, accountId);
            stm.setString(2, majorId);
            stm.setShort(3, schoolYear);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                result = new StudentEntity(schoolYear, majorId, 0);
            }
        }

        return result;
    }

    @Override
    public StudentEntity getByAccount(AccountEntity account) throws SQLException {
        if (account == null) {
            return null;
        }

        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        StudentEntity result = null;

        String sql = "SELECT school_year, major_id, experience_point " +
                "FROM account_student " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, account.getId());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                short schoolYear = resultSet.getShort(1);
                String majorId = resultSet.getString(2);
                int experiencePoint = resultSet.getInt(3);

                result = StudentEntity.builder()
                        .id(account.getId())
                        .email(account.getEmail())
                        .alternativeEmail(account.getAlternativeEmail())
                        .firstName(account.getFirstName())
                        .lastName(account.getLastName())
                        .avatarUrl(account.getAvatarUrl())
                        .description(account.getDescription())
                        .statusId(account.getStatusId())
                        .schoolYear(schoolYear)
                        .majorId(majorId)
                        .experiencePoint(experiencePoint)
                        .build();
            }
        }

        return result;
    }

    @Override
    public boolean updateStudent(StudentEntity studentEntity) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }
        String sql = "UPDATE account_student " +
                "SET school_year = ISNULL(?, school_year), " +
                "major_id = ISNULL(?, major_id), experience_point = ISNULL(?, experience_point) " +
                "WHERE id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setShort(1, studentEntity.getSchoolYear());
            stm.setString(2, studentEntity.getMajorId());
            stm.setInt(3, studentEntity.getExperiencePoint());
            stm.setString(4, studentEntity.getId());

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteStudentById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE FROM account_student " +
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
}
