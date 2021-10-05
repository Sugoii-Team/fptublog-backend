package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/lecturers")
public class LecturerResource {

    @Inject
    private IBlogService blogService;

    @GET
    @Path("/{lecturer_id}/reviewing_blogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewingBlogs(@PathParam("lecturer_id") String lecturerId) {
        List<BlogEntity> reviewingBlogs = null;

        try {
            reviewingBlogs = blogService.getReviewingBlogsOfLecturer(lecturerId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(reviewingBlogs).build();
    }
}
