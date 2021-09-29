package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ILecturerFieldDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.LecturerFieldEntity;
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
public class ImplLecturerFieldDAO implements ILecturerFieldDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<LecturerFieldEntity> getByLecturerId(String lecturerId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<LecturerFieldEntity> result = null;

        String sql = "SELECT id, field_id " +
                "FROM account_lecturer_field " +
                "WHERE lecturer_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, lecturerId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String fieldId = resultSet.getString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new LecturerFieldEntity(id, lecturerId, fieldId));
            }

        }

        return result;
    }
}
