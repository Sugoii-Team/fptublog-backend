package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.model.BlogRateModel;
import com.dsc.fptublog.service.interfaces.IBlogService;
import com.dsc.fptublog.service.interfaces.IRateService;
import com.dsc.fptublog.service.interfaces.ITagService;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/blogs")
public class BlogResource {

    @Inject
    private IBlogService blogService;

    @Inject
    private IRateService rateService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogs(@QueryParam("limit") int limit, @QueryParam("page") int page) {
        List<BlogEntity> blogList = null;
        try {
            blogList = blogService.getAllBlogs(limit, page);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(blogList).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlog(@PathParam("id") String id) {
        BlogEntity blog;
        try {
            blog = blogService.getById(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(blog).build();
    }

    @POST
    @RolesAllowed({Role.STUDENT, Role.LECTURER, Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBlog(@Context SecurityContext sc, BlogEntity newBlog) {
        String userId = sc.getUserPrincipal().getName();
        newBlog.setAuthorId(userId);

        Response response;
        try {
            newBlog = blogService.createBlog(newBlog);
            response = Response.ok(newBlog).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return response;
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBlog(@Context SecurityContext sc, @PathParam("id") String id, BlogEntity updatedBlog) {
        Response response;

        updatedBlog.setId(id);
        String userId = sc.getUserPrincipal().getName();
        BlogEntity result = null;
        try {
            result = blogService.updateBlog(userId, updatedBlog);
            if (result == null) {
                response = Response.status(Response.Status.FORBIDDEN).build();
            } else {
                response = Response.ok(result).build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @DELETE
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOwnBlog(@Context SecurityContext sc, @PathParam("id") String blogId) {
        String userId = sc.getUserPrincipal().getName();

        Response response;

        try {
            boolean result = blogService.deleteBlogOfAuthor(userId, blogId);
            if (result) {
                response = Response.ok("Delete blog successfully!").build();
            } else {
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Delete blog fail").build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/authors/{author_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnBlogs(@PathParam("author_id") String authorId,
                                @QueryParam("limit") int limit, @QueryParam("page") int page) {
        List<BlogEntity> blogList;

        try {
            blogList = blogService.getAllBlogsOfAuthor(authorId, limit, page);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(blogList).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogStatus() {
        List<BlogStatusEntity> blogStatusList;

        try {
            blogStatusList = blogService.getAllBlogStatus();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(blogStatusList).build();
    }

    @GET
    @Path("/status/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogStatus(@PathParam("id") String id) {
        Response response;

        try {
            BlogStatusEntity blogStatus = blogService.getBlogStatus(id);
            response = Response.ok(blogStatus).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/{blog_id}/rates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogsRates(@PathParam("blog_id") String blogId) {
        Response response;

        try {
            BlogRateModel blogRate = rateService.getRateOfBlog(blogId);
            response = Response.ok(blogRate).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }
}
