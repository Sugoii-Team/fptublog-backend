package com.dsc.fptublog.service.interfaces;

import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ICommentService {
    List<CommentEntity> getAllCommentsByBlogId(String blogId) throws SQLException;

    CommentEntity insertComment(CommentEntity newComment) throws SQLException;
}
