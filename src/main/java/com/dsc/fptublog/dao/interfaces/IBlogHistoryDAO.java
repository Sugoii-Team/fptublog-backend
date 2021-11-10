package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogHistory;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IBlogHistoryDAO {

    BlogHistory insertByBlogHistory(BlogHistory blogHistory) throws SQLException;

    BlogHistory getByBlogId(String blogId) throws SQLException;
}
