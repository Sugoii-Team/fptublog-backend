package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IMajorDAO;
import com.dsc.fptublog.dao.interfaces.IStudentAwardDAO;
import com.dsc.fptublog.dao.interfaces.IStudentDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.MajorEntity;
import com.dsc.fptublog.entity.StudentAwardEntity;
import com.dsc.fptublog.entity.StudentEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequestScoped
public class ImplStudentDAO implements IStudentDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IMajorDAO majorDAO;

    @Inject
    private IStudentAwardDAO studentAwardDAO;

    @Override
    public StudentEntity createByStudent(StudentEntity studentEntity) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            AccountEntity account = accountDAO.createByAccount(studentEntity);

            String sql = "INSERT INTO account_student (id, school_year, major_id) " +
                    "VALUES (?, ?, ?)";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, account.getId());
            stm.setShort(2, studentEntity.getSchoolYear());
            stm.setString(3, studentEntity.getMajor().getId());

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                studentEntity.setId(account.getId());
                return studentEntity;
            }
        }
        return null;
    }

    @Override
    public StudentEntity getById(String id) throws SQLException {
        StudentEntity result = null;
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            AccountEntity account = accountDAO.getById(id);
            if (account == null) {
                return null;
            }

            String sql = "SELECT school_year, major_id " +
                    "FROM account_student " +
                    "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                short schoolYear = resultSet.getShort("school_year");
                String majorId = resultSet.getString("major_id");
                MajorEntity major = majorDAO.getById(majorId);

                List<StudentAwardEntity> studentAwardList = studentAwardDAO.getByStudentId(id);

                result = new StudentEntity(account, schoolYear, major, studentAwardList);
            }
        }
        return result;
    }

    @Override
    public StudentEntity getByEmail(String email) throws SQLException {
        StudentEntity result = null;
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            AccountEntity account = accountDAO.getById(email);
            if (account == null) {
                return null;
            }

            String sql = "SELECT school_year, major_id " +
                    "FROM account_student " +
                    "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, account.getId());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                short schoolYear = resultSet.getShort("school_year");
                String majorId = resultSet.getString("major_id");
                MajorEntity major = majorDAO.getById(majorId);

                List<StudentAwardEntity> studentAwardList = studentAwardDAO.getByStudentId(account.getId());

                result = new StudentEntity(account, schoolYear, major, studentAwardList);
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
        StudentEntity result = null;
        if (connection != null) {
            String sql = "SELECT school_year, major_id " +
                    "FROM account_student " +
                    "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, account.getId());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                short schoolYear = resultSet.getShort("school_year");
                String majorId = resultSet.getString("major_id");

                MajorEntity major = majorDAO.getById(majorId);
                List<StudentAwardEntity> studentAwardList = studentAwardDAO.getByStudentId(account.getId());

                result = new StudentEntity(account, schoolYear, major, studentAwardList);
            }
        }
        return result;
    }

    @Override
    public boolean updateByStudent(StudentEntity studentEntity) {
        return false;
    }

    @Override
    public StudentEntity deleteById(String id) {
        return null;
    }
}
