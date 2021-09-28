package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.*;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
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
public class ImplBlogDAO implements IBlogDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;


    @Inject
    private IAccountDAO accountDAO;

    @Inject
    private IBlogStatusDAO blogStatusDAO;

    @Inject
    private ICategoryDAO categoryDAO;

    @Inject
    private IBlogTagDAO blogTagDAO;


    @Override
    public BlogEntity insertByBlog(BlogEntity newBlog) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "INSERT INTO blog (author_id, title, content, posted_datetime, status_id, category_id, reviewer_id, review_datetime, views) "
                    + "OUTPUT inserted.id " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, newBlog.getAuthor().getId());
            stm.setString(2, newBlog.getTitle());
            stm.setString(3, newBlog.getContent());
            stm.setLong(4, newBlog.getPostedDateTime());
            stm.setString(5, newBlog.getStatus().getId());
            stm.setString(6, newBlog.getCategory().getId());
            stm.setString(7, newBlog.getReviewer().getId());
            stm.setLong(8, newBlog.getReviewDateTime());
            stm.setInt(9, newBlog.getViews());

            ResultSet result = stm.executeQuery();
            if (result.next()) {
                newBlog.setId(result.getString(1));
                return newBlog;
            }
        }
        return null;
    }

    @Override
    public boolean updateByBlog(BlogEntity updatedBlog) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "UPDATE blog "
                    + "SET title = ?, content = ?, status_id = ?, category_id = ?, reviewer_id = ?, review_datetime = ?, view = ? "
                    + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, updatedBlog.getTitle());
            stm.setString(2, updatedBlog.getContent());
            stm.setString(3, updatedBlog.getStatus().getId());
            stm.setString(4, updatedBlog.getCategory().getId());
            stm.setString(5, updatedBlog.getReviewer().getId());
            stm.setLong(6, updatedBlog.getReviewDateTime());
            stm.setInt(7, updatedBlog.getViews());

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlogEntity getById(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT author_id, title, content, posted_datetime, status_id, category_id, reviewer_id, review_datetime, views "
                    + "FROM blog " + "WHERE id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, blogId);
            ResultSet result = stm.executeQuery();
            if (result.next()) {
                String authorID = result.getString("author_id");
                AccountEntity author = accountDAO.getById(authorID);
                String title = result.getNString("title");
                String content = result.getNString("content");
                Long postedDateTime = result.getLong("posted_datetime");
                String statusID = result.getString("status_id");
                BlogStatusEntity status = blogStatusDAO.getById(statusID);
                String categoryId = result.getString("category_id");
                CategoryEntity category = categoryDAO.getById(categoryId);
                String reviewerId = result.getString("reviewer_id");
                AccountEntity reviewer = accountDAO.getById(reviewerId);
                long reviewDateTime = result.getLong("review_datetime");
                int views = result.getInt("views");
                BlogEntity blog = new BlogEntity(blogId, author, title, content, postedDateTime, status, category,
                        reviewer, reviewDateTime, views, new ArrayList<>());
                boolean getBlogTagSuccessful = blogTagDAO.getByBlogAndTag(blog);
                return blog;
            }
        }
        return null;
    }

    @Override
    public boolean deletedById(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "DELETE " + "FROM blog " + "WHERE id = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, blogId);
            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<BlogEntity> getAllBlogs() throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT id, author_id, title, content, posted_datetime, status_id, category_id, reviewer_id, review_datetime, views "
                    + "FROM blog";
            PreparedStatement stm = connection.prepareStatement(sql);
            ResultSet result = stm.executeQuery();
            List<BlogEntity> blogList = new ArrayList<>();
            while (result.next()) {
                String id = result.getString("id");
                String authorID = result.getString("author_id");
                AccountEntity author = accountDAO.getById(authorID);
                String title = result.getNString("title");
                String content = result.getNString("content");
                Long postedDateTime = result.getLong("posted_datetime");
                String statusID = result.getString("status_id");
                BlogStatusEntity status = blogStatusDAO.getById(statusID);
                String categoryId = result.getString("category_id");
                CategoryEntity category = categoryDAO.getById(categoryId);
                String reviewerId = result.getString("reviewer_id");
                AccountEntity reviewer = accountDAO.getById(reviewerId);
                long reviewDateTime = result.getLong("review_datetime");
                int views = result.getInt("views");
                BlogEntity blog = new BlogEntity(id, author, title, content, postedDateTime, status, category,
                        reviewer, reviewDateTime, views, new ArrayList<>());
                boolean getBlogTagSuccessful = blogTagDAO.getByBlogAndTag(blog);
                blogList.add(blog);
            }
            return blogList;
        }
        return null;
    }


}
