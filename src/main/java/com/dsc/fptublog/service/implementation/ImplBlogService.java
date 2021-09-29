package com.dsc.fptublog.service.implementation;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import lombok.extern.log4j.Log4j;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Service
public class ImplBlogService implements IBlogService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IBlogDAO blogDAO;

    @Override
    public BlogEntity getByName(String name) {
        return null;
    }

    @Override
    public List<BlogEntity> getAllBlogs() throws SQLException {
        List<BlogEntity> blogList = null;
        try {
            connectionWrapper.beginTransaction();
            blogList = blogDAO.getAllBlogs();
            connectionWrapper.commit();
            return blogList;
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
    }
}
