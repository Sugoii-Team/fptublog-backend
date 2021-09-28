package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ILecturerAwardDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.LecturerAwardEntity;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImplLecturerAwardDAO implements ILecturerAwardDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<LecturerAwardEntity> getByLecturerId(String lecturerId) throws SQLException {
        List<LecturerAwardEntity> result = null;
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT id, award_id " +
                         "FROM lecturer_award " +
                         "WHERE lecturer_id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, lecturerId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String award_id = resultSet.getString("id");

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new LecturerAwardEntity(id, lecturerId, award_id));
            }
        }
        return result;
    }
}
