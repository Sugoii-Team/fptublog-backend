package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogTagEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;


@Contract
public interface IBlogTagDAO {

    List<BlogTagEntity> getByBlogId(String blogId) throws SQLException;
}
