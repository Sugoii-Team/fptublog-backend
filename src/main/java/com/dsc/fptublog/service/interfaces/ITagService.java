package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.TagEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ITagService {

    List<TagEntity> getAllTagsOfBlog(String blogId) throws SQLException;

    List<TagEntity> getTags() throws SQLException;

    TagEntity getTag(String id) throws SQLException;

    List<TagEntity> getTopTag(int limit, int page) throws SQLException;

    TagEntity createTag(TagEntity newTag) throws SQLException;

    List<TagEntity> createTagListForBlog(String blogId, List<TagEntity> tagList) throws SQLException;

    List<TagEntity> updateTagListForBlog(String authorId, String blogId, List<TagEntity> tagList) throws SQLException;

    List<BlogEntity> getBlogsOfTag(String tagId, int limit, int page) throws SQLException;
}
