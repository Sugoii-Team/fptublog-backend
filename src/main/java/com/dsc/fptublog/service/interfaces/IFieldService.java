package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.FieldEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IFieldService {

    List<FieldEntity> getAllFields() throws SQLException;

    FieldEntity getFieldById(String id) throws SQLException;

    List<FieldEntity> getLecturerFields(String lecturerId) throws SQLException;

    boolean addLecturerFields(String lecturerId, List<FieldEntity> fieldList) throws SQLException;

    List<FieldEntity> getTopFields() throws SQLException;

    List<LecturerEntity> getLecturersByFieldId(String fieldId) throws SQLException;

    boolean updateField(FieldEntity updateField) throws SQLException;

    FieldEntity createField(FieldEntity newField) throws SQLException;

    boolean deleteField(String fieldId) throws SQLException;

}
