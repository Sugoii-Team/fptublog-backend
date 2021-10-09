package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.dao.interfaces.IBlogStatusDAO;
import com.dsc.fptublog.dao.interfaces.IBlogTagDAO;
import com.dsc.fptublog.dao.interfaces.ICategoryDAO;
import com.dsc.fptublog.dao.interfaces.ILecturerFieldDAO;
import com.dsc.fptublog.dao.interfaces.ITagDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.entity.CategoryEntity;
import com.dsc.fptublog.entity.LecturerFieldEntity;
import com.dsc.fptublog.entity.TagEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScoped
public class ImplBlogService implements IBlogService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IBlogDAO blogDAO;

    @Inject
    private IBlogStatusDAO blogStatusDAO;

    @Inject
    private IBlogTagDAO blogTagDAO;

    @Inject
    private ITagDAO tagDAO;

    @Inject
    private ILecturerFieldDAO lecturerFieldDAO;

    @Inject
    private ICategoryDAO categoryDAO;

    @Override
    public BlogEntity getById(String id) throws SQLException {
        BlogEntity blog;

        try {
            connectionWrapper.beginTransaction();

            blog = blogDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blog;
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

        return blogList.stream()
                .filter(blog -> approvedStatus.getId().equals(blog.getStatusId()))
                .collect(Collectors.toList());
    }

    @Override
    public BlogEntity createBlog(BlogEntity newBlog) throws SQLException {
        BlogStatusEntity pendingStatus;

        try {
            connectionWrapper.beginTransaction();

            pendingStatus = blogStatusDAO.getByName("pending");
            newBlog.setStatusId(pendingStatus.getId());

            long createdDateTime = System.currentTimeMillis();
            newBlog.setCreatedDateTime(createdDateTime);

            newBlog.setViews(0);

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

    @Override
    public boolean createTagListForBlog(String blogId, List<TagEntity> tagList) throws SQLException {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();

            // check existed tagList. If not existed, insert new
            tagList = tagDAO.insertIfNotExistedByTagList(tagList);
            if (tagList != null) {
                // Don't need to check exist blogId because JDBC will throw exception for us
                result = blogTagDAO.createByBlogIdAndTagList(blogId, tagList);
            }

            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    private List<String> getCategoryOfLecturer(String lecturerId) throws SQLException {
        // get lecturer's fieldsId
        List<LecturerFieldEntity> lecturerFieldList = lecturerFieldDAO.getByLecturerId(lecturerId);
        if (lecturerFieldList == null) {
            return Collections.emptyList();
        }
        List<String> fieldIdList = lecturerFieldList.stream()
                .map(LecturerFieldEntity::getFieldId)
                .collect(Collectors.toList());

        // get field's categories
        List<CategoryEntity> categoryList = categoryDAO.getByFieldIdList(fieldIdList);
        if (categoryList == null) {
            return Collections.emptyList();
        }

        return categoryList.stream()
                .map(CategoryEntity::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogEntity> getReviewingBlogsOfLecturer(String lecturerId) throws SQLException {
        List<BlogEntity> result;

        try {
            connectionWrapper.beginTransaction();

            // get lecturer's categoryId list
            List<String> categoryIdList = getCategoryOfLecturer(lecturerId);

            // get pending blogs of these categories
            BlogStatusEntity pendingStatus = blogStatusDAO.getByName("pending");
            String pendingStatusId = pendingStatus.getId();
            List<BlogEntity> blogList = blogDAO.getByCategoryIdList(categoryIdList);
            if (blogList == null) {
                return Collections.emptyList();
            }
            result = blogList.stream()
                    .filter(blog -> pendingStatusId.equals(blog.getStatusId()))
                    .collect(Collectors.toList());
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public List<BlogStatusEntity> getAllBlogStatus() throws SQLException {
        List<BlogStatusEntity> blogStatusList;

        try {
            connectionWrapper.beginTransaction();

            blogStatusList = blogStatusDAO.getAll();

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blogStatusList;
    }

    @Override
    public boolean updateIfNotNull(BlogEntity updatedBlog) throws SQLException {
        boolean result;
        try {
            connectionWrapper.beginTransaction();

            result = blogDAO.updateByBlog(updatedBlog);

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
    public boolean updateReviewStatus(BlogEntity updatedBlog) throws SQLException {
        boolean result = false;

        try {
            connectionWrapper.beginTransaction();

            // Get full blog info
            BlogEntity oldBlog = blogDAO.getById(updatedBlog.getId());
            String pendingStatusId = blogStatusDAO.getByName("pending").getId();
            if (!pendingStatusId.equals(oldBlog.getStatusId())) {
                return false;
            }

            // Get lecturer's categoryId List
            List<String> categoryIdList = getCategoryOfLecturer(updatedBlog.getReviewerId());

            // Check this lecturer can review this blog
            if (!categoryIdList.contains(oldBlog.getCategoryId())) {
                return false;
            }

            // Check valid new status Id
            if (blogStatusDAO.getById(updatedBlog.getStatusId()) == null) {
                return false;
            }

            // Update review status and datetime
            long reviewDateTime = System.currentTimeMillis();
            updatedBlog.setReviewDateTime(reviewDateTime);
            result = blogDAO.updateByBlog(updatedBlog);

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
    public List<BlogEntity> getAllBlogsOfAuthor(String authorId) throws SQLException {
        List<BlogEntity> result;

        try {
            connectionWrapper.beginTransaction();

            result = blogDAO.getByAuthorId(authorId);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result != null ? result : Collections.emptyList();
    }
}
