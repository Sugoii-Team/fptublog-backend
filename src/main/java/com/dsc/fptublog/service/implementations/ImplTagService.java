package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.IBlogTagDAO;
import com.dsc.fptublog.dao.interfaces.ITagDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.BlogTagEntity;
import com.dsc.fptublog.entity.TagEntity;
import com.dsc.fptublog.service.interfaces.ITagService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImplTagService implements ITagService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private IBlogTagDAO blogTagDAO;

    @Inject
    private ITagDAO tagDAO;

    @Override
    public List<TagEntity> getAllTagsOfBlog(String blogId) throws SQLException {
        List<BlogTagEntity> blogTagList;
        List<TagEntity> tagList;

        try {
            connectionWrapper.beginTransaction();

            // get intermediate table data by blogId
            blogTagList = blogTagDAO.getByBlogId(blogId);

            // et Tag list by tagId from intermediate table
            List<String> tagIdList = blogTagList.stream().map(BlogTagEntity::getTagId).collect(Collectors.toList());

            tagList = tagDAO.getByIdList(tagIdList);

            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }

        return tagList;
    }
}
