package com.dsc.fptublog.service.interfaces;

import com.dsc.fptublog.entity.CommentEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ICommentService {
    public List<CommentEntity> getAllCommentsByBlogId(String blogId) throws SQLException;
}
