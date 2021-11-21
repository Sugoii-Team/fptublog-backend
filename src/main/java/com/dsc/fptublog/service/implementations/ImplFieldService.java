package com.dsc.fptublog.service.implementations;


import com.dsc.fptublog.dao.interfaces.IFieldDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerFieldDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.FieldEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.entity.LecturerFieldEntity;
import com.dsc.fptublog.service.interfaces.IFieldService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
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

    @Inject
    private ILecturerDAO lecturerDAO;

    @Override
    public List<FieldEntity> getAllFields() throws SQLException {
        List<FieldEntity> result;
        try {
            connectionWrapper.beginTransaction();

            result = fieldDAO.getAll();

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
    public FieldEntity getFieldById(String id) throws SQLException {
        FieldEntity result;
        try {
            connectionWrapper.beginTransaction();

            result = fieldDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

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

            if (lecturerFieldDAO.deleteByLecturerId(lecturerId)) {
                if (lecturerFieldDAO.addByLecturerIdAndFieldList(lecturerId, fieldList)) {
                    result = true;
                    connectionWrapper.commit();
                }
            }
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public List<FieldEntity> getTopFields() throws SQLException {
        List<FieldEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = fieldDAO.getTopFields();

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
    public List<LecturerEntity> getLecturersByFieldId(String fieldId) throws SQLException {
        List<LecturerEntity> lecturersList = new ArrayList<>();
        try {
            connectionWrapper.beginTransaction();
            List<String> lecturersIdList= lecturerFieldDAO.getLecturersIdByFieldId(fieldId);
            if(lecturersIdList == null){
                return null;
            }
            for (String lecturerId : lecturersIdList) {
                LecturerEntity lecturer = lecturerDAO.getById(lecturerId);
                lecturersList.add(lecturer);
            }
            connectionWrapper.commit();
            return lecturersList;
        }finally {
            connectionWrapper.close();
        }
    }

    @Override
    public boolean updateField(FieldEntity updateField) throws SQLException {
        boolean result = false;
        try{
            connectionWrapper.beginTransaction();
            result = fieldDAO.updateField(updateField);
            connectionWrapper.commit();
        }catch (SQLException ex){
            connectionWrapper.rollback();
            throw ex;
        }finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public FieldEntity createField(FieldEntity newField) throws SQLException {
        FieldEntity result = null;
        try{
            connectionWrapper.beginTransaction();
            result = fieldDAO.createField(newField);
            connectionWrapper.commit();
        }catch (SQLException ex){
            connectionWrapper.rollback();
            throw ex;
        }finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public boolean deleteField(String fieldId) throws SQLException {
        return false;
    }


}
