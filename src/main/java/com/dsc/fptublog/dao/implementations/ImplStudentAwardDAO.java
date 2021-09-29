package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IStudentAwardDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.StudentAwardEntity;
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
public class ImplStudentAwardDAO implements IStudentAwardDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public List<StudentAwardEntity> getByStudentId(String studentId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<StudentAwardEntity> result = null;

        String sql = "SELECT id, award_id " +
                "FROM student_award " +
                "WHERE student_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, studentId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String awardId = resultSet.getString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(new StudentAwardEntity(id, studentId, awardId));
            }
        }

        return result;
    }
}
