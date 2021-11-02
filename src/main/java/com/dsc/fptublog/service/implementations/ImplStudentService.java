package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IAccountDAO;
import com.dsc.fptublog.dao.interfaces.IMajorDAO;
import com.dsc.fptublog.dao.interfaces.IStudentDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.MajorEntity;
import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.service.interfaces.IStudentService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplStudentService implements IStudentService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IStudentDAO studentDAO;

    @Inject
    private IAccountDAO accountDAO;

    @Override
    public List<StudentEntity> getAllStudents() throws SQLException {
        List<StudentEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = studentDAO.getAll();

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public StudentEntity getStudent(String id) throws SQLException {
        AccountEntity account;
        StudentEntity student;
        try {
            connectionWrapper.beginTransaction();
            account = accountDAO.getById(id);
            student = studentDAO.getByAccount(account);
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return student;
    }

    @Override
    public StudentEntity updateStudent(StudentEntity studentEntity) throws SQLException {
        try {
            connectionWrapper.beginTransaction();
            boolean result1 = accountDAO.updateByAccount(studentEntity);
            boolean result2 = studentDAO.updateStudent(studentEntity);
            if (result1 && result2) {
                connectionWrapper.commit();
                return studentEntity;
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return null;
    }
}
