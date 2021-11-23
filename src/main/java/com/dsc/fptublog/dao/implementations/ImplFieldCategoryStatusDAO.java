package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IFieldCategoryStatusDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.FieldCategoryStatusEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequestScoped
public class ImplFieldCategoryStatusDAO implements IFieldCategoryStatusDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Override
    public FieldCategoryStatusEntity getById(String id) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        FieldCategoryStatusEntity fieldCategoryStatusEntity;
        if (connection == null){
            return null;
        }
        String sql = "SELECT name "
                    +"FROM field_category_status "
                    +"WHERE id = ?";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1,id);
            ResultSet result = stm.executeQuery();
            if(result.next()){
                String name = result.getString(2);
                fieldCategoryStatusEntity = new FieldCategoryStatusEntity(id,name);
                return fieldCategoryStatusEntity;
            }
        }
        return null;
    }

    @Override
    public FieldCategoryStatusEntity getByName(String name) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        FieldCategoryStatusEntity fieldCategoryStatusEntity;
        if (connection == null){
            return null;
        }
        String sql = "SELECT id "
                +"FROM field_category_status "
                +"WHERE name = ?";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1,name);
            ResultSet result = stm.executeQuery();
            if(result.next()){
                String id = result.getString(1);
                fieldCategoryStatusEntity = new FieldCategoryStatusEntity(id,name);
                return fieldCategoryStatusEntity;
            }
        }
        return null;
    }
}
