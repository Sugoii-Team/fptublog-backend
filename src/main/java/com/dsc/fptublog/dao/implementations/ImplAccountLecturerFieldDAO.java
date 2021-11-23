package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountLecturerFieldDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
@RequestScoped
public class ImplAccountLecturerFieldDAO implements IAccountLecturerFieldDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;


    @Override
    public boolean deleteAccountLecturerField(String fieldId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }
        String sql = "DELETE "
                + "FROM account_lecturer_field "
                + "WHERE field_id = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, fieldId);
            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }
}
