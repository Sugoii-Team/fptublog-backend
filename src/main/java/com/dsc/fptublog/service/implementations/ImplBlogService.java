package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.dao.interfaces.IBlogStatusDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImplBlogService implements IBlogService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IBlogDAO blogDAO;

    @Inject
    private IBlogStatusDAO blogStatusDAO;

    @Override
    public BlogEntity getById(String id) throws SQLException {
        BlogEntity blog;
        BlogStatusEntity approvedStatus;

        try {
            connectionWrapper.beginTransaction();

            blog = blogDAO.getById(id);
            approvedStatus = blogStatusDAO.getByName("approved");

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        if (approvedStatus.getId().equals(blog.getStatusId())) {
            return blog;
        }
        return null;
    }

    @Override
    public List<BlogEntity> getAllBlogs() throws SQLException {
        List<BlogEntity> blogList = null;
        BlogStatusEntity approvedStatus;

        try {
            connectionWrapper.beginTransaction();

            blogList = blogDAO.getAllBlogs();
            approvedStatus = blogStatusDAO.getByName("approved");

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blogList.stream().filter(blog -> {
            return approvedStatus.getId().equals(blog.getStatusId());
        }).collect(Collectors.toList());
    }

    @Override
    public BlogEntity createBlog(BlogEntity newBlog) throws SQLException {
        BlogStatusEntity pendingStatus;

        try {
            connectionWrapper.beginTransaction();

            pendingStatus = blogStatusDAO.getByName("pending");
            newBlog.setStatusId(pendingStatus.getId());
            newBlog = blogDAO.insertByBlog(newBlog);

            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }

        return newBlog;
    }
}
