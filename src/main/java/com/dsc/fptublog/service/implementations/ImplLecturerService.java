package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.ILecturerDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.service.interfaces.ILecturerService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplLecturerService implements ILecturerService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private ILecturerDAO lecturerDAO;

    @Override
    public List<LecturerEntity> getAllLecturers() throws SQLException {
        List<LecturerEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = lecturerDAO.getAll();

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
    public LecturerEntity getLecturer(String id) throws SQLException {
        LecturerEntity result;

        try {
            connectionWrapper.beginTransaction();

            result = lecturerDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return result;
    }
}
