package com.dsc.fptublog.dao.interfaces;


import com.dsc.fptublog.entity.CommentEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ICommentDAO {
    public CommentEntity insertNewComment(String blogId, String authorId, String content, long postedDatetime, String statusId, String replyTo) throws SQLException;

    public List<CommentEntity> getAllCommentsByBlogId(String blogId) throws SQLException;
}
