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
@Path("/students")
public class StudentResource {

    @Inject
    private IBlogService blogService;

    @GET
    @Path("/{student_id}/blogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnBlogs(@PathParam("student_id") String studentId) {
        List<BlogEntity> blogList;

        try {
            blogList = blogService.getAllBlogsOfAuthor(studentId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(blogList).build();
    }
}
