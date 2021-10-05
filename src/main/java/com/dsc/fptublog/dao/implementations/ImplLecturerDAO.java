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

@Service
@RequestScoped
public class ImplLecturerDAO implements ILecturerDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public LecturerEntity getById(String id) {
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
}
