package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogTagEntity;
import com.dsc.fptublog.entity.TagEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;


@Contract
public interface IBlogTagDAO {

    List<BlogTagEntity> getByBlogId(String blogId) throws SQLException;

    boolean createByBlogIdAndTagList(String blogId, List<TagEntity> tagList) throws SQLException;
}
