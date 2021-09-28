package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ILecturerFieldDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.LecturerFieldEntity;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImplLecturerFieldDAO implements ILecturerFieldDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<LecturerFieldEntity> getByLecturerId(String lecturerId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        List<LecturerFieldEntity> result = null;
        if (connection != null) {
            String sql = "SELECT id, field_id " +
                         "FROM account_lecturer_field " +
                         "WHERE lecturer_id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, lecturerId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String fieldId = resultSet.getString("field_id");
                LecturerFieldEntity lecturerField = new LecturerFieldEntity(id, lecturerId, fieldId);
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(lecturerField);
            }
        }
        return result;
    }
}
