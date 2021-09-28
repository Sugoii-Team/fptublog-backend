package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;


@Contract
public interface IBlogTagDAO {

    public boolean getByBlogAndTag(BlogEntity blog) throws SQLException;
}
