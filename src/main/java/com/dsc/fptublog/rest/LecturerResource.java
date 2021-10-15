package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/lecturers")
public class LecturerResource {

    @Inject
    private IBlogService blogService;

    @GET
    @RolesAllowed(Role.LECTURER)
    @Path("/{lecturer_id}/reviewingBlogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewingBlogs(@Context SecurityContext sc, @PathParam("lecturer_id") String lecturerId) {
        if (!sc.getUserPrincipal().getName().equals(lecturerId)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<BlogEntity> reviewingBlogs;

        try {
            reviewingBlogs = blogService.getReviewingBlogsOfLecturer(lecturerId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(reviewingBlogs).build();
    }

    @PUT
    @Path("/{lecturer_id}/reviewingBlogs/{blog_id}")
    @RolesAllowed(Role.LECTURER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReviewingBlogStatus(@Context SecurityContext sc, @PathParam("lecturer_id") String lecturerId,
                                     @PathParam("blog_id") String blogId, BlogEntity updatedBlog) {
        if (!sc.getUserPrincipal().getName().equals(lecturerId)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        updatedBlog.setId(blogId);
        updatedBlog.setReviewerId(lecturerId);
        boolean result;

        try {
            result = blogService.updateReviewStatus(updatedBlog);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        if (result) {
            return Response.ok("Update successfully!").build();
        } else {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Update fail!").build();
        }
    }
}
