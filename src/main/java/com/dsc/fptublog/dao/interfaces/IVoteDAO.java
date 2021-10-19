package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.VoteEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;

@Contract
public interface IVoteDAO {

    VoteEntity getByAccountIdAndBlogId(String accountId, String blogId) throws SQLException;

    VoteEntity deleteByAccountIdAndBlogId(String accountId, String blogId) throws SQLException;

    VoteEntity insertByVoteEntity(VoteEntity vote) throws SQLException;
}
