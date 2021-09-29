package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

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
        } finally {
            connectionWrapper.close();
        }
        return blogList;
    }
}
