package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogRateEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IBlogRateDAO {

    List<BlogRateEntity> getBlogRateByBlogId(String blogId) throws SQLException;
}
