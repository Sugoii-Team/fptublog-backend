package com.dsc.fptublog.dao.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogTagDAO;
import com.dsc.fptublog.dao.interfaces.ITagDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogTagEntity;
import com.dsc.fptublog.entity.TagEntity;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


@Service
@RequestScoped
public class ImplBlogTagDAO implements IBlogTagDAO {

    @Inject
    private ConnectionWrapper connectionWrapper;


    @Inject
    private ITagDAO tagDAO;


    @Override
    public void getByBlogAndTag(BlogEntity blog) throws SQLException {
        /*Connection connection = connectionWrapper.getConnection();
        if (connection != null) {
            String sql = "SELECT id, blog_id, tag_id "
                    + "FROM blog_tag "
                    + "WHERE blog_id = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, blog.getId());
            ResultSet result = stm.executeQuery();
            while (result.next()) {
                String id = result.getString("id");
                String blogId = result.getString("blog_id");
                String tagId = result.getString("tag_id");
                BlogTagEntity blogTags = new BlogTagEntity(id, blogId, tagId);
                TagEntity tag = tagDAO.getById(tagId);
                blog.setBlogTags(new ArrayList<>());
                tag.setBlogTags(new ArrayList<>());
                blog.getBlogTags().add(blogTags);
                tag.getBlogTags().add(blogTags);
            }
        }*/

    }
}
