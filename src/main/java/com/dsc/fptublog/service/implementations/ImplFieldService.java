package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IFieldDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerFieldDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.FieldEntity;
import com.dsc.fptublog.entity.LecturerFieldEntity;
import com.dsc.fptublog.service.interfaces.IFieldService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScoped
public class ImplFieldService implements IFieldService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private ILecturerFieldDAO lecturerFieldDAO;

    @Inject
    private IFieldDAO fieldDAO;

    @Override
    public List<FieldEntity> getLecturerFields(String lecturerId) throws SQLException {
        List<FieldEntity> result;

        try {
            connectionWrapper.beginTransaction();

            List<LecturerFieldEntity> lecturerFieldList = lecturerFieldDAO.getByLecturerId(lecturerId);
            if (lecturerFieldList == null) {
                return Collections.emptyList();
            }
            List<String> fieldIdList = lecturerFieldList.stream()
                    .map(LecturerFieldEntity::getFieldId)
                    .collect(Collectors.toList());

            result = fieldDAO.getByFieldIdList(fieldIdList);

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
    public boolean addLecturerFields(String lecturerId, List<FieldEntity> fieldList) throws SQLException {
        boolean result = false;

        try {
            connectionWrapper.beginTransaction();

            // remove all duplicate id
            fieldList = fieldList.stream().distinct().collect(Collectors.toList());

            if (lecturerFieldDAO.addByLecturerIdAndFieldList(lecturerId, fieldList)) {
                result = true;
                connectionWrapper.commit();
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }

        return result;
    }
}
