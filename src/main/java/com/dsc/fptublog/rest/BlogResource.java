package com.dsc.fptublog.rest;


import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/blogs")
public class BlogResource {

    @Inject
    private IBlogService blogService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogs() {
        List<BlogEntity> blogList = null;
        try {
            blogList = blogService.getAllBlogs();
            return Response.ok(blogList).build();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
    }
}
