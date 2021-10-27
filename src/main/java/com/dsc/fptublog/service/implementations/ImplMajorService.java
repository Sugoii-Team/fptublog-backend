package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IMajorDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.MajorEntity;
import com.dsc.fptublog.service.interfaces.IMajorService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplMajorService implements IMajorService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IMajorDAO majorDAO;

    @Override
    public List<MajorEntity> getAllMajor() throws SQLException {
        List<MajorEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = majorDAO.getAll();

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
    public MajorEntity getMajor(String id) throws SQLException {
        MajorEntity result;

        try {
            connectionWrapper.beginTransaction();

            result = majorDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }
}
