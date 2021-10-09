package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.CommentEntity;
import com.dsc.fptublog.service.interfaces.ICommentService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/blogs")
public class CommentResource {

    @Inject
    private ICommentService commentService;

    @GET
    @Path("/{id}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllComments(@PathParam("id") String blogId) {
        List<CommentEntity> commentsList;
        try {
            commentsList = commentService.getAllCommentsByBlogId(blogId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(commentsList).build();
    }

    @POST
    @Path("/{id}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createComment(CommentEntity newComment) {
        try {
            newComment = commentService.insertComment(newComment);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(newComment).build();
    }
}
