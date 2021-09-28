package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IStudentAwardDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.StudentAwardEntity;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ImplStudentAwardDAO implements IStudentAwardDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<StudentAwardEntity> getByStudentId(String studentId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        List<StudentAwardEntity> result = null;
        if (connection != null) {
            String sql = "SELECT id, award_id " +
                         "FROM student_award " +
                         "WHERE student_id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, studentId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String awardId = resultSet.getString("award_id");
                StudentAwardEntity studentAward = new StudentAwardEntity(id, studentId, awardId);
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(studentAward);
            }
        }
        return result;
    }
}
