package com.dsc.fptublog.service.implementations;

import com.dsc.fptublog.dao.interfaces.ICommentDAO;
import com.dsc.fptublog.database.ConnectionWrapper;
import com.dsc.fptublog.entity.CommentEntity;
import com.dsc.fptublog.service.interfaces.ICommentService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;


@Service
public class ImplCommentService implements ICommentService {

    @Inject
    private ConnectionWrapper connectionWrapper;

    @Inject
    private ICommentDAO commentDAO;

    @Override
    public List<CommentEntity> getAllCommentsByBlogId(String blogId) throws SQLException {
        List<CommentEntity> commentsList;
        try {
            connectionWrapper.beginTransaction();
            commentsList = commentDAO.getAllCommentsByBlogId(blogId);
            connectionWrapper.commit();
        } finally {
            connectionWrapper.close();
        }
        return commentsList;
    }

    @Override
    public CommentEntity insertComment(CommentEntity newComment) throws SQLException {
        CommentEntity comment;
        try{
            connectionWrapper.beginTransaction();
            comment = commentDAO.insertComment(newComment);
            connectionWrapper.commit();
        }catch (SQLException ex){
            connectionWrapper.rollback();
            throw ex;
        }
        finally {
            connectionWrapper.close();
        }
        return comment;
    }


}
