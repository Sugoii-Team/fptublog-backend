package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAdminDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AdminEntity;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class ImplAdminDAO implements IAdminDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public boolean checkAuthentication(AdminEntity admin) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "SELECT username " +
                "FROM admin " +
                "WHERE username = ? AND password = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, admin.getUsername());
            stm.setString(2, admin.getPassword());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }

        return false;
    }
}
