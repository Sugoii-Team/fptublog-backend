package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.ICategoryDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.CategoryEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequestScoped
public class ImplCategoryDAO implements ICategoryDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public CategoryEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        CategoryEntity result = null;

        String sql = "SELECT name, field_id "
                + "FROM category "
                + "WHERE id = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, id);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getNString(1);
                String fieldId = resultSet.getString(2);
                result = CategoryEntity.builder().id(id).name(name).fieldId(fieldId).build();
            }
        }

        return result;
    }

    @Override
    public List<CategoryEntity> getAll() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<CategoryEntity> result = null;

        String sql = "SELECT id, name, field_id " +
                "FROM category " +
                "WHERE status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);
                String fieldId = resultSet.getString(3);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(CategoryEntity.builder().id(id).name(name).fieldId(fieldId).build());
            }
        }

        return result;
    }

    @Override
    public List<CategoryEntity> getByFieldIdList(List<String> fieldIdList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<CategoryEntity> result = null;

        String sql = "SELECT id, name " +
                "FROM category " +
                "WHERE field_id = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (String fieldId : fieldIdList) {
                stm.setString(1, fieldId);

                ResultSet resultSet = stm.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString(1);
                    String name = resultSet.getNString(2);

                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(CategoryEntity.builder().id(id).name(name).fieldId(fieldId).build());
                }
            }
        }
        return result;
    }

    @Override
    public List<CategoryEntity> getByFieldId(String fieldId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<CategoryEntity> result = null;

        String sql = "SELECT id, name " +
                "FROM category " +
                "WHERE field_id = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, fieldId);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getNString(2);

                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(CategoryEntity.builder().id(id).name(name).fieldId(fieldId).build());
            }
        }

        return result;
    }

    @Override
    public boolean updateCategory(CategoryEntity updateCategory) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if(connection == null){
            return false;
        }
        String sql ="UPDATE category "
                    +"SET name = ISNULL(?, name), field_id = ISNULL(?, field_id) "
                    +"WHERE id = ?";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1, updateCategory.getName());
            stm.setString(2, updateCategory.getFieldId());
            stm.setString(3, updateCategory.getId());
            int effectRow = stm.executeUpdate();
            if(effectRow > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity newCategory) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if(connection == null){
            return null;
        }
        String sql ="INSERT INTO category (name, field_id, status_id) "
                    +"OUTPUT inserted.id "
                    +"VALUES (?, ?, ?)";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1, newCategory.getName());
            stm.setString(2, newCategory.getFieldId());
            stm.setString(3, newCategory.getStatusId());
            ResultSet result = stm.executeQuery();
            if(result.next()){
                String categoryId = result.getString(1);
                newCategory.setId(categoryId);
                return newCategory;
            }
        }
        return null;
    }

    @Override
    public boolean deleteCategory(CategoryEntity category) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if(connection == null){
            return false;
        }
        String sql ="UPDATE category "
                +"SET status_id = ISNUll(?, status_id) "
                +"WHERE id = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1, category.getStatusId());
            stm.setString(2,category.getId());
            int effectRow = stm.executeUpdate();
            if(effectRow > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public CategoryEntity getByName(String name) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        CategoryEntity result = null;

        String sql = "SELECT id, field_id "
                + "FROM category "
                + "WHERE name = ? AND status_id = (SELECT id FROM field_category_status WHERE name = 'active')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, name);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                String id = resultSet.getString(1);
                String fieldId = resultSet.getString(2);
                result = CategoryEntity.builder().id(id).name(name).fieldId(fieldId).build();
            }
        }

        return result;
    }

}
