package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogHistory;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.entity.CategoryEntity;
import com.dsc.fptublog.entity.LecturerFieldEntity;
import com.dsc.fptublog.model.ReviewModel;
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
    private ILecturerFieldDAO lecturerFieldDAO;

    @Inject
    private ICategoryDAO categoryDAO;

    @Inject
    private IBlogHistoryDAO blogHistoryDAO;

    @Inject
    private IBlogTagDAO blogTagDAO;

    @Inject
    private IAccountDAO accountDAO;

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
    public List<BlogEntity> getAllBlogs(int limit, int page) throws SQLException {
        List<BlogEntity> blogList = null;

        try {
            connectionWrapper.beginTransaction();

            int offset = limit * (page - 1);
            blogList = blogDAO.getAllBlogs(limit, offset);
            if (blogList == null) {
                blogList = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blogList;
    }

    @Override
    public List<BlogEntity> getAllBlogs(int limit, int page, String title) throws SQLException {
        List<BlogEntity> blogList = null;

        try {
            connectionWrapper.beginTransaction();

            int offset = limit * (page - 1);
            blogList = blogDAO.getByTitle(limit, offset, title);
            if (blogList == null) {
                blogList = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blogList;
    }

    @Override
    public List<BlogEntity> getTopBlogs(int limit, int page) throws SQLException {
        List<BlogEntity> blogList = null;

        try {
            connectionWrapper.beginTransaction();

            int offset = limit * (page - 1);
            blogList = blogDAO.getTopBlogs(limit, offset);
            if (blogList == null) {
                blogList = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blogList;
    }

    @Override
    public BlogEntity createBlog(BlogEntity newBlog) throws SQLException {
        BlogStatusEntity pendingStatus;

        try {
            connectionWrapper.beginTransaction();

            // create blog history list
            long createdDateTime = System.currentTimeMillis();
            BlogHistory blogHistory =
                    blogHistoryDAO.insertByBlogHistory(new BlogHistory(null, newBlog.getAuthorId(), newBlog.getCategoryId(), createdDateTime, 0, 0));
            newBlog.setHistoryId(blogHistory.getId());

            pendingStatus = blogStatusDAO.getByName("pending approved");
            newBlog.setStatusId(pendingStatus.getId());

            newBlog.setCreatedDateTime(createdDateTime);
            newBlog.setUpdatedDatetime(createdDateTime);

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
            result = blogDAO.getPendingBlogByCategoryIdList(categoryIdList);
            if (result == null) {
                result = Collections.emptyList();
            }
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
            if (blogStatusList == null) {
                blogStatusList = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return blogStatusList;
    }

    @Override
    public BlogStatusEntity getBlogStatus(String id) throws SQLException {
        BlogStatusEntity result;

        try {
            connectionWrapper.beginTransaction();

            result = blogStatusDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
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

    private boolean processApprove(BlogEntity oldBlog, String pendingDeletedStatusId,
                                   String pendingUpdatedStatusId, String pendingApprovedStatusId)
            throws SQLException {

        // pending deleting
        if (oldBlog.getStatusId().equals(pendingDeletedStatusId)) {
            String deletedStatusId = blogStatusDAO.getByName("deleted").getId();

            oldBlog.setStatusId(deletedStatusId);
            oldBlog.setReviewDateTime(System.currentTimeMillis());

            if (blogDAO.updateByBlog(oldBlog)) {
                return accountDAO.updateNumberOfBlogAndAvgRate(oldBlog.getAuthorId());
            } else {
                return false;
            }
        }

        // pending updated
        if (oldBlog.getStatusId().equals(pendingUpdatedStatusId)) {
            if (!blogDAO.hideBlogInHistory(oldBlog.getHistoryId())) {
                return false;
            }
        }

        // pending update or pending approved
        String approvedStatusId = blogStatusDAO.getByName("approved").getId();
        oldBlog.setStatusId(approvedStatusId);
        oldBlog.setReviewDateTime(System.currentTimeMillis());

        if (blogDAO.updateByBlog(oldBlog)) {
            return accountDAO.updateNumberOfBlogAndAvgRate(oldBlog.getAuthorId());
        } else {
            return false;
        }
    }

    private boolean processReject(BlogEntity oldBlog, String pendingDeletedStatusId) throws SQLException {
        //pending deleted
        if (oldBlog.getStatusId().equals(pendingDeletedStatusId)) {
            String approvedStatusId = blogStatusDAO.getByName("approved").getId();

            oldBlog.setStatusId(approvedStatusId);
            oldBlog.setReviewDateTime(System.currentTimeMillis());

            return blogDAO.updateByBlog(oldBlog);
        }

        //pending approved or pending updated
        String draftStatusId = blogStatusDAO.getByName("draft").getId();

        oldBlog.setStatusId(draftStatusId);
        oldBlog.setReviewDateTime(System.currentTimeMillis());

        return blogDAO.updateByBlog(oldBlog);
    }

    @Override
    public boolean updateReviewStatus(ReviewModel reviewModel, String reviewerId, String blogId) throws SQLException {
        boolean result = false;

        try {
            connectionWrapper.beginTransaction();

            // Get full blog info
            BlogEntity oldBlog = blogDAO.getById(blogId);

            // Get lecturer's categoryId List
            List<String> categoryIdList = getCategoryOfLecturer(reviewerId);

            // Check this lecturer can review this blog
            if (!categoryIdList.contains(oldBlog.getCategoryId()) || oldBlog.getAuthorId().equals(reviewerId)) {
                return false;
            }

            String pendingApprovedStatusId = blogStatusDAO.getByName("pending approved").getId();
            String pendingDeletedStatusId = blogStatusDAO.getByName("pending deleted").getId();
            String pendingUpdatedStatusId = blogStatusDAO.getByName("pending updated").getId();

            if (!oldBlog.getStatusId().equals(pendingApprovedStatusId) &&
                    !oldBlog.getStatusId().equals(pendingDeletedStatusId) &&
                    !oldBlog.getStatusId().equals(pendingUpdatedStatusId)) {
                return false;
            }

            oldBlog.setReviewerId(reviewerId);
            if ("approve".equals(reviewModel.getAction())) {
                result = processApprove(oldBlog, pendingDeletedStatusId,
                        pendingUpdatedStatusId, pendingApprovedStatusId);
            }
            if ("reject".equals(reviewModel.getAction())) {
                result = processReject(oldBlog, pendingDeletedStatusId);
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

    @Override
    public List<BlogEntity> getAllBlogsOfAuthor(String authorId, int limit, int page,
                                                String sortByField, String orderByType) throws SQLException {
        List<BlogEntity> result;

        try {
            connectionWrapper.beginTransaction();

            int offset = limit * (page - 1);
            if (sortByField != null && orderByType != null) {
                if ("created_datetime".equals(sortByField)) {
                    sortByField = "history.created_datetime";
                }
                result = blogDAO.getByAuthorId(authorId, limit, offset, sortByField, orderByType);
            } else {
                result = blogDAO.getByAuthorId(authorId, limit, offset);
            }
            if (result == null) {
                result = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public List<BlogEntity> getAllApprovedBlogsOfAuthor(String authorId, int limit, int page,
                                                        String sortByField, String orderByType) throws SQLException {
        List<BlogEntity> result;

        try {
            connectionWrapper.beginTransaction();

            int offset = limit * (page - 1);
            if (sortByField != null && orderByType != null) {
                if ("created_datetime".equals(sortByField)) {
                    sortByField = "history.created_datetime";
                }
                result = blogDAO.getApprovedBlogByAuthorId(authorId, limit, offset, sortByField, orderByType);
            } else {
                result = blogDAO.getApprovedBlogByAuthorId(authorId, limit, offset);
            }
            if (result == null) {
                result = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return result;
    }

    @Override
    public boolean deleteBlogOfAuthor(String authorId, String blogId) throws SQLException {
        boolean result = false;
        BlogEntity deletedBlog;
        try {
            connectionWrapper.beginTransaction();

            // get blog by id to check right author
            deletedBlog = blogDAO.getById(blogId);
            if (deletedBlog != null && deletedBlog.getAuthorId().equals(authorId)) {
                // approved
                String approvedStatusId = blogStatusDAO.getByName("approved").getId();
                if (approvedStatusId.equals(deletedBlog.getStatusId())) {
                    String pendingDeletedStatusId = blogStatusDAO.getByName("pending deleted").getId();
                    deletedBlog.setStatusId(pendingDeletedStatusId);
                    result = blogDAO.updateByBlog(deletedBlog);
                }

                // draft
                String draftStatusId = blogStatusDAO.getByName("draft").getId();
                if (draftStatusId.equals(deletedBlog.getStatusId())) {
                    blogTagDAO.deleteByBlogId(deletedBlog.getId());
                    result = blogDAO.deletedById(deletedBlog.getId());
                }

                // pending approved
                String pendingApprovedStatusId = blogStatusDAO.getByName("pending approved").getId();
                if (pendingApprovedStatusId.equals(deletedBlog.getStatusId())) {
                    deletedBlog.setStatusId(draftStatusId);
                    result = blogDAO.updateByBlog(deletedBlog);
                }

                // pending updated
                String pendingUpdatedStatusId = blogStatusDAO.getByName("pending updated").getId();
                if (pendingUpdatedStatusId.equals(deletedBlog.getStatusId())) {
                    deletedBlog.setStatusId(draftStatusId);
                    result = blogDAO.updateByBlog(deletedBlog);
                }

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

    @Override
    public BlogEntity updateBlog(String authorId, BlogEntity updatedBlog) throws Exception {
        BlogEntity result = null;
        try {
            connectionWrapper.beginTransaction();

            // get oldBlog, then check authorId is right
            BlogEntity oldBlog = blogDAO.getById(updatedBlog.getId());
            if (!oldBlog.getAuthorId().equals(authorId)) {
                return null;
            }

            long currentTime = System.currentTimeMillis();

            oldBlog.setThumbnailUrl(updatedBlog.getThumbnailUrl());
            oldBlog.setTitle(updatedBlog.getTitle());
            oldBlog.setContent(updatedBlog.getContent());
            oldBlog.setDescription(updatedBlog.getDescription());
            oldBlog.setUpdatedDatetime(currentTime);
            oldBlog.setReviewerId(null);
            oldBlog.setReviewDateTime(0);

            String draftStatusId = blogStatusDAO.getByName("draft").getId();
            String pendingApprovedStatusId = blogStatusDAO.getByName("pending approved").getId();
            String pendingUpdatedStatusId = blogStatusDAO.getByName("pending updated").getId();
            String pendingDeletedStatusId = blogStatusDAO.getByName("pending deleted").getId();

            if (draftStatusId.equals(oldBlog.getStatusId())) {
                if (blogDAO.isApproved(oldBlog.getId())) {
                    oldBlog.setStatusId(pendingUpdatedStatusId);
                } else {
                    oldBlog.setStatusId(pendingApprovedStatusId);
                }

                if (blogDAO.updateByBlog(oldBlog)) {
                    result = oldBlog;
                }
            } else if (pendingApprovedStatusId.equals(oldBlog.getStatusId()) || pendingUpdatedStatusId.equals(oldBlog.getStatusId())) {
                if (blogDAO.updateByBlog(oldBlog)) {
                    result = oldBlog;
                }
            } else if (pendingDeletedStatusId.equals(oldBlog.getStatusId())) {
                throw new Exception("Cannot update a pending deleted blog");
            } else { // its status is approved
                // Check if existed another "pending update" of it -> override to this update
                if (blogDAO.isExistedPendingUpdateBlogInTheSameBlogHistory(oldBlog.getId())) {
                    String pendingUpdatedBlogId = blogDAO.getPendingUpdateBlogIdInTheSameHistory(oldBlog.getId());
                    oldBlog.setId(pendingUpdatedBlogId);
                }

                oldBlog.setStatusId(pendingUpdatedStatusId);
                // update to DB
                result = blogDAO.insertByBlog(oldBlog);
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

    @Override
    public List<BlogEntity> getBlogsOfFields(String fieldId, int limit, int page) throws SQLException {
        List<BlogEntity> result = null;

        try {
            connectionWrapper.beginTransaction();

            int offset = limit * (page - 1);
            result = blogDAO.getApprovedBlogByFieldId(fieldId, limit, offset);

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
    public boolean undoPendingDeleted(String userId, String blogId) throws Exception {
        boolean result = false;
        try {
            connectionWrapper.beginTransaction();

            BlogEntity pendingDeletedBlog = blogDAO.getById(blogId);
            if (!userId.equals(pendingDeletedBlog.getAuthorId())) {
                throw new Exception("The user does not own this blog");
            }

            String pendingDeletedStatutsId = blogStatusDAO.getByName("pending deleted").getId();
            if (!pendingDeletedStatutsId.equals(pendingDeletedBlog.getStatusId())) {
                throw new Exception("This blog is not in pending deleted status");
            }

            // undo pending delete is set status to approved
            String approvedStatusId = blogStatusDAO.getByName("approved").getId();
            pendingDeletedBlog.setStatusId(approvedStatusId);
            if (blogDAO.updateByBlog(pendingDeletedBlog)) {
                result = true;
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

    @Override
    public boolean deleteBlog(BlogEntity deleteBlog) throws SQLException {
        boolean result;
        BlogStatusEntity deleteStatus;
        try {
            connectionWrapper.beginTransaction();
            deleteStatus = blogStatusDAO.getByName("deleted"); // get delete status
            deleteBlog.setStatusId(deleteStatus.getId()); // set Blog's status to delete
            result = blogDAO.updateByBlog(deleteBlog); // change blog's status to delete in database
            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return result;
    }
}
