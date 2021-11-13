package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.dao.interfaces.ICategoryDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.CategoryEntity;
import com.dsc.fptublog.service.interfaces.ICategoryService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
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
}
