package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogDAO;
import com.dsc.fptublog.dao.interfaces.IBlogTagDAO;
import com.dsc.fptublog.dao.interfaces.ITagDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogTagEntity;
import com.dsc.fptublog.entity.TagEntity;
import com.dsc.fptublog.service.interfaces.ITagService;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScoped
public class ImplTagService implements ITagService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IBlogTagDAO blogTagDAO;

    @Inject
    private ITagDAO tagDAO;

    @Inject
    private IBlogDAO blogDAO;

    @Override
    public List<TagEntity> getAllTagsOfBlog(String blogId) throws SQLException {
        List<BlogTagEntity> blogTagList;
        List<TagEntity> tagList = null;

        try {
            connectionWrapper.beginTransaction();

            // get intermediate table data by blogId
            blogTagList = blogTagDAO.getByBlogId(blogId);

            if (blogTagList != null) {
                // get Tag list by tagId from intermediate table
                List<String> tagIdList = blogTagList.stream().map(BlogTagEntity::getTagId).collect(Collectors.toList());
                tagList = tagDAO.getByIdList(tagIdList);
            }

            if (tagList == null) {
                tagList = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return tagList;
    }

    @Override
    public List<TagEntity> getTags() throws SQLException {
        List<TagEntity> tagList;

        try {
            connectionWrapper.beginTransaction();

            tagList = tagDAO.getAll();
            if (tagList == null) {
                tagList = Collections.emptyList();
            }

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return tagList;
    }

    @Override
    public TagEntity getTag(String id) throws SQLException {
        TagEntity tag;

        try {
            connectionWrapper.beginTransaction();

            tag = tagDAO.getById(id);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return tag;
    }

    @Override
    public TagEntity createTag(TagEntity newTag) throws SQLException {
        try {
            connectionWrapper.beginTransaction();

            newTag = tagDAO.insertByTag(newTag);

            connectionWrapper.commit();
        } catch (SQLException ex) {
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
        return newTag;
    }

    @Override
    public List<TagEntity> createTagListForBlog(String blogId, List<TagEntity> tagList) throws SQLException {
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

        return result ? tagList : null;
    }

    @Override
    public List<TagEntity> updateTagListForBlog(String authorId, String blogId, List<TagEntity> tagList)
            throws SQLException {
        boolean result = false;

        try {
            connectionWrapper.beginTransaction();

            // check right authorId
            BlogEntity blog = blogDAO.getById(blogId);
            if (!authorId.equals(blog.getAuthorId())) {
                return null;
            }

            // replace blog_tag table for this blog
            // remove old blog_tag rows
            blogTagDAO.deleteByBlogId(blogId);

            // after delete old rows, insert updated rows
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

        return result ? tagList : null;
    }
}
