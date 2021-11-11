package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IStudentDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.model.Top30DaysStudentModel;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<Top30DaysStudentModel> getTop30Days(long before30DaysTimeStamp) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<Top30DaysStudentModel> result = null;

        String sql = "SELECT account.id, COUNT(history.id) AS blogs_number_in_30_days " +
                "FROM account " +
                "INNER JOIN account_status ON account.status_id = account_status.id " +
                "INNER JOIN blog_history history ON account.id = history.author_id " +
                "INNER JOIN blog ON history.id = blog.blog_history_id " +
                "INNER JOIN blog_status ON blog.status_id = blog_status.id " +
                "WHERE account_status.name = 'activated' AND account.role = 'STUDENT' " +
                "AND history.created_datetime > ? " +
                "AND (blog_status.name = 'approved' OR blog_status.name = 'pending deleted') " +
                "GROUP BY account.id " +
                "ORDER BY blogs_number_in_30_days DESC";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, before30DaysTimeStamp);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                int numberOfBlogIn30Days = resultSet.getInt(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(
                        new Top30DaysStudentModel(StudentEntity.builder().id(id).build(), numberOfBlogIn30Days)
                );
            }
        }

        return result;
    }

    @Override
    public StudentEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT email, alternative_email, firstname, lastname, avatar_url, description, " +
                "status_id, role, blogs_number, avg_rate, school_year, major_id, experience_point " +
                "FROM account " +
                "INNER JOIN account_student student on account.id = student.id " +
                "WHERE account.id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String email = resultSet.getString(1);
                String alternativeEmail = resultSet.getString(2);
                String firstname = resultSet.getNString(3);
                String lastname = resultSet.getNString(4);
                String avatarUrl = resultSet.getString(5);
                String description = resultSet.getString(6);
                String statusId = resultSet.getString(7);
                String role = resultSet.getString(8);
                int blogsNumber = resultSet.getInt(9);
                float avgRate = resultSet.getFloat(10);
                byte schoolYear = resultSet.getByte(11);
                String majorId = resultSet.getString(12);
                int experiencePoint = resultSet.getInt(13);

                return StudentEntity.builder().id(id).email(email).alternativeEmail(alternativeEmail)
                        .firstName(firstname).lastName(lastname).avatarUrl(avatarUrl).description(description)
                        .statusId(statusId).role(role).blogsNumber(blogsNumber).avgRate(avgRate).schoolYear(schoolYear)
                        .majorId(majorId).experiencePoint(experiencePoint).build();
            }
        }

        return null;
    }
}
