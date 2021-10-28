package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ILecturerDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
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
public class ImplLecturerDAO implements ILecturerDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<LecturerEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<LecturerEntity> result = null;

        String sql = "SELECT account.id, account.email, account.alternative_email, account.firstname, " +
                "account.lastname, account.avatar_url, account.description, account.status_id, account.role " +
                "FROM account_lecturer lecturer " +
                "INNER JOIN account on account.id = lecturer.id " +
                "INNER JOIN account_status status on status.id = account.status_id " +
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

                LecturerEntity lecturer = LecturerEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstname)
                        .lastName(lastname)
                        .avatarUrl(avatarUrl)
                        .description(description)
                        .statusId(statusId)
                        .role(role)
                        .build();

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(lecturer);
            }
        }

        return result;
    }

    @Override
    public LecturerEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "SELECT account.email, account.alternative_email, account.firstname, " +
                "account.lastname, account.avatar_url, account.description, account.status_id, account.role " +
                "FROM account_lecturer lecturer " +
                "INNER JOIN account on account.id = lecturer.id " +
                "INNER JOIN account_status status on status.id = account.status_id " +
                "WHERE account.id = ? AND status.name = 'activated'";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String email = resultSet.getString(1);
                String alternativeEmail = resultSet.getString(2);
                String firstname = resultSet.getNString(3);
                String lastname = resultSet.getNString(4);
                String avatarUrl = resultSet.getString(5);
                String description = resultSet.getNString(6);
                String statusId = resultSet.getString(7);
                String role = resultSet.getString(8);

                LecturerEntity lecturer = LecturerEntity.builder()
                        .id(id)
                        .email(email)
                        .alternativeEmail(alternativeEmail)
                        .firstName(firstname)
                        .lastName(lastname)
                        .avatarUrl(avatarUrl)
                        .description(description)
                        .statusId(statusId)
                        .role(role)
                        .build();

                return lecturer;
            }
        }

        return null;
    }

    @Override
    public LecturerEntity getByEmail(String email) {
        return null;
    }

    @Override
    public LecturerEntity getByAccount(AccountEntity account) throws SQLException {
        if (account == null) {
            return null;
        }

        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        LecturerEntity result = null;

        String sql = "SELECT id " +
                "FROM account_lecturer " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, account.getId());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                result = LecturerEntity.builder()
                        .id(account.getId())
                        .email(account.getEmail())
                        .alternativeEmail(account.getAlternativeEmail())
                        .firstName(account.getFirstName())
                        .lastName(account.getLastName())
                        .avatarUrl(account.getAvatarUrl())
                        .description(account.getDescription())
                        .statusId(account.getStatusId())
                        .build();
            }
        }

        return result;
    }

    @Override
    public LecturerEntity insertById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        LecturerEntity result = null;

        String sql = "INSERT INTO account_lecturer (id)" +
                "VALUES (?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                result = new LecturerEntity();
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

        String sql = "DELETE FROM account_lecturer " +
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
