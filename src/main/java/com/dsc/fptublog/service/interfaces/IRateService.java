package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.model.BlogRateModel;
import com.dsc.fptublog.model.VoteModel;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IRateService {

    BlogRateModel getRateOfBlog(String blogId) throws SQLException;

    VoteModel getVoteOfUserForBlog(String userId, String blogId) throws SQLException;

    boolean addVoteForBlog(String userId, String blogId, String star) throws SQLException;

    boolean deleteVoteForBlog(String userId, String blogId) throws SQLException;
}
