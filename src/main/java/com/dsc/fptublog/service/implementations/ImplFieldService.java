package com.dsc.fptublog.service.implementations;


import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.*;
import com.dsc.fptublog.service.interfaces.ICategoryService;
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

    @Inject
    private IAccountLecturerFieldDAO accountLecturerFieldDAO;

    @Inject
    private IFieldCategoryStatusDAO fieldCategoryStatusDAO;


    @Inject
    private ICategoryDAO categoryDAO;


    @Inject
    private IBlogDAO blogDAO;

    @Inject
    private IBlogStatusDAO blogStatusDAO;


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
            List<String> lecturersIdList = lecturerFieldDAO.getLecturersIdByFieldId(fieldId);
            if (lecturersIdList == null) {
                return null;
            }
            for (String lecturerId : lecturersIdList) {
                LecturerEntity lecturer = lecturerDAO.getById(lecturerId);
                lecturersList.add(lecturer);
            }
            connectionWrapper.commit();
            return lecturersList;
        } finally {
            connectionWrapper.close();
        }
    }

    @Override
    public boolean updateField(FieldEntity updateField) throws SQLException {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();
            result = fieldDAO.updateField(updateField);
            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public FieldEntity createField(FieldEntity newField) throws SQLException {
        FieldEntity result = null;
        FieldCategoryStatusEntity activeStatus;
        try {
            connectionWrapper.beginTransaction();
            activeStatus = fieldCategoryStatusDAO.getByName("active");
            newField.setStatusId(activeStatus.getId());
            result = fieldDAO.createField(newField);
            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public boolean deleteField(String fieldId) throws SQLException {
        FieldEntity deleteField;
        FieldCategoryStatusEntity inactiveStatus;
        boolean resultDeleteAccountLecturerField;
        boolean resultDeleteField;
        boolean isSuccessful = false;
        boolean resultDeleteBlog = false;
        boolean resultDeletedCategory = false;
        try {
            connectionWrapper.beginTransaction();
            deleteField = fieldDAO.getById(fieldId);
            inactiveStatus = fieldCategoryStatusDAO.getByName("inactive");
            deleteField.setStatusId(inactiveStatus.getId());
            resultDeleteField = fieldDAO.updateField(deleteField);
            resultDeleteAccountLecturerField = accountLecturerFieldDAO.deleteAccountLecturerField(fieldId); // delete in database
            List<CategoryEntity> deleteCategories = categoryDAO.getByFieldId(fieldId);
            BlogStatusEntity deleteStatus = blogStatusDAO.getByName("deleted"); // get delete status
            if (deleteCategories == null) { // if fields has no categories
                resultDeletedCategory = true;
                resultDeleteBlog = true;
            } else {
                for (CategoryEntity deleteCategory : deleteCategories) {
                    deleteCategory.setStatusId(inactiveStatus.getId()); //set inactive status delete
                    resultDeletedCategory = categoryDAO.deleteCategory(deleteCategory); // update category status in database
                    List<BlogEntity> deleteBlogs = blogDAO.getByCategoryId(deleteCategory.getId());
                    if (deleteBlogs != null) { // if that category has blogs
                        for (BlogEntity deleteBlog : deleteBlogs) {
                            deleteBlog.setStatusId(deleteStatus.getId()); // set Blog's status to delete
                            resultDeleteBlog = blogDAO.updateByBlog(deleteBlog); // change blog's status to delete in database
                            if (resultDeleteBlog == false) {
                                break;
                            }
                        }
                    } else { //if category has no blogs
                        resultDeleteBlog = true;
                    }
                }
            }
            isSuccessful = (resultDeleteField && resultDeleteAccountLecturerField && resultDeletedCategory && resultDeleteBlog);
            connectionWrapper.commit();
            return isSuccessful;
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
    }


}
