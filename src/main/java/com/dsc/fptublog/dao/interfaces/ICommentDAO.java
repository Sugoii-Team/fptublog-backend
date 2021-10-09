package com.dsc.fptublog.dao.interfaces;

import com.dsc.fptublog.entity.CommentEntity;
import org.jvnet.hk2.annotations.Contract;

import java.sql.SQLException;
import java.util.List;

@Contract
public interface ICommentDAO {

    public List<CommentEntity> getAllCommentsByBlogId(String blogId) throws SQLException;

    public CommentEntity insertComment(CommentEntity newBlog) throws SQLException;
}
