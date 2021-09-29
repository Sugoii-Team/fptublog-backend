package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.TagEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ITagService {

    List<TagEntity> getAllTagsOfBlog(String blogId) throws SQLException;
}
