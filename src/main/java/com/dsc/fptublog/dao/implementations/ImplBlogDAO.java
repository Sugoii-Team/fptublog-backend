package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
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

    private BlogEntity getBlogWithContent(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("blog_id");
        String authorId = resultSet.getString("author_id");
        String thumbnailUrl = resultSet.getString("thumbnail_url");
        String title = resultSet.getNString("title");
        String content = resultSet.getNString("content");
        String description = resultSet.getNString("description");
        long updatedDatetime = resultSet.getLong("updated_datetime");
        String statusId = resultSet.getString("status_id");
        String categoryId = resultSet.getString("category_id");
        String reviewerId = resultSet.getString("reviewer_id");
        long reviewDateTime = resultSet.getLong("review_datetime");
        String historyId = resultSet.getString("blog_history_id");
        long createdDatetime = resultSet.getLong("created_datetime");
        int views = resultSet.getInt("views");
        float avgRate = resultSet.getFloat("avg_rate");

        return new BlogEntity(id, authorId, thumbnailUrl, title, content, description, updatedDatetime,
                statusId, categoryId, reviewerId, reviewDateTime, historyId, createdDatetime, views, avgRate);
    }

    private BlogEntity getBlogWithoutContent(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("blog_id");
        String authorId = resultSet.getString("author_id");
        String thumbnailUrl = resultSet.getString("thumbnail_url");
        String title = resultSet.getNString("title");
        String description = resultSet.getNString("description");
        long updatedDatetime = resultSet.getLong("updated_datetime");
        String statusId = resultSet.getString("status_id");
        String categoryId = resultSet.getString("category_id");
        String reviewerId = resultSet.getString("reviewer_id");
        long reviewDateTime = resultSet.getLong("review_datetime");
        String historyId = resultSet.getString("blog_history_id");
        long createdDatetime = resultSet.getLong("created_datetime");
        int views = resultSet.getInt("views");
        float avgRate = resultSet.getFloat("avg_rate");

        return new BlogEntity(id, authorId, thumbnailUrl, title, null, description, updatedDatetime,
                statusId, categoryId, reviewerId, reviewDateTime, historyId, createdDatetime, views, avgRate);
    }

    @Override
    public BlogEntity insertByBlog(BlogEntity newBlog) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        String sql = "INSERT INTO blog (author_id, thumbnail_url, title, content, description, created_datetime, " +
                "status_id, category_id, reviewer_id, review_datetime, blog_history_id) "
                + "OUTPUT inserted.id "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, newBlog.getAuthorId());
            stm.setString(2, newBlog.getThumbnailUrl());
            stm.setNString(3, newBlog.getTitle());
            stm.setNString(4, newBlog.getContent());
            stm.setNString(5, newBlog.getDescription());
            stm.setLong(6, newBlog.getUpdatedDatetime());
            stm.setString(7, newBlog.getStatusId());
            stm.setString(8, newBlog.getCategoryId());
            stm.setString(9, newBlog.getReviewerId());
            stm.setLong(10, newBlog.getReviewDateTime());
            stm.setString(11, newBlog.getHistoryId());

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                newBlog.setId(resultSet.getString(1));
                return newBlog;
            }
        }

        return null;
    }

    @Override
    public boolean updateByBlog(BlogEntity updatedBlog) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE blog "
                + "SET thumbnail_url = ISNULL(?, thumbnail_url) , title = ISNULL(?, title), " +
                "content = ISNULL(?, content), description = ISNULL(?, description), " +
                "status_id = ISNULL(?, status_id), category_id = ISNULL(?, category_id), " +
                "reviewer_id = ISNULL(?, reviewer_id), review_datetime = ISNULL(?, review_datetime), " +
                "blog_history_id = ISNULL(?, blog_history_id) "
                + "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, updatedBlog.getThumbnailUrl());
            stm.setNString(2, updatedBlog.getTitle());
            stm.setNString(3, updatedBlog.getContent());
            stm.setNString(4, updatedBlog.getDescription());
            stm.setString(5, updatedBlog.getStatusId());
            stm.setString(6, updatedBlog.getCategoryId());
            stm.setString(7, updatedBlog.getReviewerId());
            stm.setLong(8, updatedBlog.getReviewDateTime());
            stm.setString(9, updatedBlog.getHistoryId());
            stm.setString(10, updatedBlog.getId());

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
        if (connection == null) {
            return null;
        }

        BlogEntity result = null;

        String sql = "SELECT blog.id AS blog_id, author_id, thumbnail_url, title, content, description, " +
                "blog.created_datetime AS updated_datetime, status_id, category_id, reviewer_id, review_datetime, " +
                "blog_history_id, history.created_datetime AS created_datetime, views, avg_rate " +
                "FROM blog INNER JOIN blog_history history on history.id = blog.blog_history_id " +
                "WHERE blog.id = ? AND status_id != (SELECT id FROM blog_status WHERE name = 'deleted')";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                result = this.getBlogWithContent(resultSet);
            }
        }
        return result;
    }

    @Override
    public boolean deletedById(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "DELETE " +
                "FROM blog " +
                "WHERE id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogId);

            int effectRow = stm.executeUpdate();
            if (effectRow > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<BlogEntity> getAllBlogs(int limit, int offset) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT blog.id AS blog_id, author_id, thumbnail_url, title, description, " +
                "blog.created_datetime AS updated_datetime, status_id, category_id, reviewer_id, review_datetime, " +
                "blog_history_id, history.created_datetime AS created_datetime, views, avg_rate " +
                "FROM blog " +
                "INNER JOIN blog_status status ON blog.status_id = status.id " +
                "INNER JOIN blog_history history ON blog.blog_history_id = history.id " +
                "WHERE status.name = 'approved' OR status.name = 'pending deleted' " +
                "ORDER BY blog.id DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, offset);
            stm.setInt(2, limit);

            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                BlogEntity blog = this.getBlogWithoutContent(resultSet);
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(blog);
            }
        }

        return result;
    }

    @Override
    public List<BlogEntity> getTopBlogs(int limit, int offset) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT blog.id AS blog_id, author_id, thumbnail_url, title, description, " +
                "blog.created_datetime AS updated_datetime, status_id, category_id, reviewer_id, review_datetime, " +
                "blog_history_id, history.created_datetime AS created_datetime, views, avg_rate " +
                "FROM blog " +
                "INNER JOIN blog_status status ON blog.status_id = status.id " +
                "INNER JOIN blog_history history ON blog.blog_history_id = history.id " +
                "WHERE status.name = 'approved' OR status.name = 'pending deleted' " +
                "ORDER BY avg_rate DESC, blog.id DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, offset);
            stm.setInt(2, limit);

            ResultSet resultSet = stm.executeQuery();

            while (resultSet.next()) {
                BlogEntity blog = this.getBlogWithoutContent(resultSet);
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(blog);
            }
        }

        return result;
    }

    /*@Override
    public List<BlogEntity> getByCategoryIdList(List<String> categoryIdList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT id, author_id, thumbnail_url, title, description, created_datetime, status_id, " +
                "reviewer_id, review_datetime, views " +
                "FROM blog " +
                "WHERE category_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (var categoryId : categoryIdList) {
                stm.setString(1, categoryId);

                ResultSet resultSet = stm.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString(1);
                    String authorId = resultSet.getString(2);
                    String thumbnailUrl = resultSet.getString(3);
                    String title = resultSet.getNString(4);
                    String description = resultSet.getNString(5);
                    long createdDatetime = resultSet.getLong(6);
                    String statusId = resultSet.getString(7);
                    String reviewerId = resultSet.getString(8);
                    long reviewDatetime = resultSet.getLong(9);
                    int views = resultSet.getInt(10);

                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(new BlogEntity(id, authorId, thumbnailUrl, title, null, description,
                            createdDatetime, statusId, categoryId, reviewerId, reviewDatetime, views));
                }
            }
        }

        return result;
    }*/

    @Override
    public List<BlogEntity> getPendingBlogByCategoryIdList(List<String> categoryIdList) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT blog.id AS blog_id, author_id, thumbnail_url, title, description, " +
                "blog.created_datetime AS updated_datetime, status_id, category_id, reviewer_id, review_datetime, " +
                "blog_history_id, history.created_datetime AS created_datetime, views, avg_rate " +
                "FROM blog " +
                "INNER JOIN blog_status status on status.id = blog.status_id " +
                "INNER JOIN blog_history history on history.id = blog.blog_history_id " +
                "WHERE blog.category_id = ? AND (status.name = 'pending approved' " +
                "OR status.name = 'pending deleted' OR status.name = 'pending updated' )";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            for (var categoryId : categoryIdList) {
                stm.setString(1, categoryId);

                ResultSet resultSet = stm.executeQuery();
                while (resultSet.next()) {
                    BlogEntity blog = this.getBlogWithoutContent(resultSet);
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(blog);
                }
            }
        }

        return result;
    }

    @Override
    public List<BlogEntity> getByAuthorId(String authorId, int limit, int offset) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT blog.id AS blog_id, author_id, thumbnail_url, title, description, " +
                "blog.created_datetime AS updated_datetime, status_id, category_id, reviewer_id, review_datetime, " +
                "blog_history_id, history.created_datetime AS created_datetime, views, avg_rate " +
                "FROM blog " +
                "INNER JOIN blog_status status on status.id = blog.status_id " +
                "INNER JOIN blog_history history on history.id = blog.blog_history_id " +
                "WHERE author_id = ? AND status.name != 'deleted' AND status.name != 'hidden' " +
                "ORDER BY blog.id " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, authorId);
            stm.setInt(2, offset);
            stm.setInt(3, limit);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                BlogEntity blog = this.getBlogWithoutContent(resultSet);
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(blog);
            }
        }

        return result;
    }

    @Override
    public List<BlogEntity> getByAuthorId(String authorId, int limit, int offset,
                                          String sortByField, String orderByType) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return null;
        }

        List<BlogEntity> result = null;

        String sql = "SELECT blog.id AS blog_id, author_id, thumbnail_url, title, description, " +
                "blog.created_datetime AS updated_datetime, status_id, category_id, reviewer_id, review_datetime, " +
                "blog_history_id, history.created_datetime AS created_datetime, views, avg_rate " +
                "FROM blog " +
                "INNER JOIN blog_status status on status.id = blog.status_id " +
                "INNER JOIN blog_history history on history.id = blog.blog_history_id " +
                "WHERE author_id = ? AND status.name != 'deleted' AND status.name != 'hidden' " +
                "ORDER BY " + sortByField + " " + orderByType + " " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, authorId);
            stm.setInt(2, offset);
            stm.setInt(3, limit);

            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()) {
                BlogEntity blog = this.getBlogWithoutContent(resultSet);
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(blog);
            }
        }

        return result;
    }

    @Override
    public BlogEntity blogIdIsExistent(String blogId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        ResultSet result = null;

        String sql = "SELECT 1 "
                    +"FROM blog "
                    +"WHERE id = ?";
        try(PreparedStatement stm = connection.prepareStatement(sql)){
            stm.setString(1,blogId);
            result = stm.executeQuery();
            if(result.next()){
                return BlogEntity.builder().id(blogId).build();
            }
        }
        return null;
    }
  
    public boolean hideBlogInHistory(String blogHistoryId) throws SQLException {
        Connection connection = connectionWrapper.getConnection();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE blog " +
                "SET status_id = (SELECT id FROM blog_status WHERE name = 'hidden') " +
                "WHERE blog_history_id = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, blogHistoryId);

            int effectedRow = stm.executeUpdate();
            if (effectedRow > 0) {
                return true;
            }
        }

        return false;
    }
}
