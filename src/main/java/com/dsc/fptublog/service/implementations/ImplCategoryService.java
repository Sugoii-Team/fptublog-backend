package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.dao.interfaces.IBlogStatusDAO;
import com.dsc.fptublog.dao.interfaces.ICategoryDAO;
import com.dsc.fptublog.dao.interfaces.IFieldCategoryStatusDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.entity.CategoryEntity;
import com.dsc.fptublog.entity.FieldCategoryStatusEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import com.dsc.fptublog.service.interfaces.ICategoryService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequestScoped
public class ImplCategoryService implements ICategoryService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private ICategoryDAO categoryDAO;

    @Inject
    private IBlogDAO blogDAO;

    @Inject
    private IFieldCategoryStatusDAO fieldCategoryStatusDAO;

    @Inject
    private IBlogService blogService;

    @Inject
    private IBlogStatusDAO blogStatusDAO;

    @Override
    public CategoryEntity getCategory(String id) throws SQLException {
        CategoryEntity category;

        try {
            connectionWrapper.beginTransaction();

            category = categoryDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return category;
    }

    @Override
    public List<CategoryEntity> getCategories() throws SQLException {
        List<CategoryEntity> categoryList;

        try {
            connectionWrapper.beginTransaction();

            categoryList = categoryDAO.getAll();

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return categoryList;
    }

    @Override
    public List<CategoryEntity> getCategoriesOfField(String fieldId) throws SQLException {
        List<CategoryEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = categoryDAO.getByFieldId(fieldId);

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
    public List<BlogEntity> getBlogsByCategoryId(String categoryId) throws SQLException {
        List<BlogEntity> result;

        try{
            connectionWrapper.beginTransaction();
            result = blogDAO.getByCategoryId(categoryId);
            connectionWrapper.commit();
        }finally {
            connectionWrapper.close();
        }
        return result;
    }

    @Override
    public boolean updateCategory(CategoryEntity updateCategory) throws SQLException {
        boolean result;
        try {
            connectionWrapper.beginTransaction();
            result = categoryDAO.updateCategory(updateCategory);
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
    public List<CategoryEntity> createCategory(List<CategoryEntity> newCategories) throws SQLException {
        List<CategoryEntity> resultList = new ArrayList<>();
        FieldCategoryStatusEntity activeStatus;
        try {
            connectionWrapper.beginTransaction();
            activeStatus = fieldCategoryStatusDAO.getByName("active");
            for(CategoryEntity newCategory : newCategories){
                newCategory.setStatusId(activeStatus.getId());
                CategoryEntity result = categoryDAO.createCategory(newCategory);
                if(result == null){
                    return null;
                }
                resultList.add(result);
            }
            connectionWrapper.commit();
        }catch (SQLException ex){
            connectionWrapper.rollback();
            throw ex;
        }finally {
            connectionWrapper.close();
        }
        return resultList;
    }

    @Override
    public boolean deleteCategory(String categoryId) throws SQLException {
        boolean resultDeletedCategory;
        FieldCategoryStatusEntity inactiveStatus;
        CategoryEntity deleteCategory;
        List<BlogEntity> deleteBlogs;
        BlogStatusEntity deleteStatus;
        boolean resultDeleteBlog = false;
        boolean isSuccessful = false;
        try {
            connectionWrapper.beginTransaction();
            inactiveStatus = fieldCategoryStatusDAO.getByName("inactive");
            deleteCategory = categoryDAO.getById(categoryId); // get category
            if(deleteCategory == null){
                return false;
            }
            deleteCategory.setStatusId(inactiveStatus.getId()); // set category status to inactive
            resultDeletedCategory = categoryDAO.deleteCategory(deleteCategory); // update category status in database
            deleteBlogs = blogDAO.getByCategoryId(categoryId); // get all blogs of deleted category
            if(deleteBlogs == null){ //if category has no blogs
                resultDeleteBlog = true;
            }else{
                for(BlogEntity deleteBlog : deleteBlogs) {
                    deleteStatus = blogStatusDAO.getByName("deleted"); // get delete status
                    deleteBlog.setStatusId(deleteStatus.getId()); // set Blog's status to delete
                    resultDeleteBlog = blogDAO.updateByBlog(deleteBlog); // change blog's status to delete in database
                    if (resultDeleteBlog == false) {
                        break;
                    }
                }
            }
            connectionWrapper.commit();
            isSuccessful = (resultDeletedCategory && resultDeleteBlog);
        }catch (SQLException ex){
            connectionWrapper.rollback();
            throw ex;
        }finally {
            connectionWrapper.close();
        }
        return isSuccessful;
    }
}
