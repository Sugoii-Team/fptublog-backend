package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.BlogRateEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface IBlogRateDAO {

    BlogRateEntity getByBlogIdAndRateID(String blogId, String rateId) throws SQLException;

    List<BlogRateEntity> getByBlogId(String blogId) throws SQLException;

    boolean decreaseAmount(String blogId, String rateId) throws SQLException;

    boolean insertByBlogIdAndRateID(String blogId, String rateId) throws SQLException;

    boolean increaseAmount(String blogId, String rateId) throws SQLException;
}
