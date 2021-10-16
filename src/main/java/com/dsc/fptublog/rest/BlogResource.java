package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.entity.TagEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import com.dsc.fptublog.service.interfaces.ITagService;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
@Path("/blogs")
public class BlogResource {

    @Inject
    private IBlogService blogService;

    @Inject
    private ITagService tagService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogs() {
        List<BlogEntity> blogList = null;
        try {
            blogList = blogService.getAllBlogs();
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
    @RolesAllowed(Role.STUDENT)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnBlogs(@PathParam("author_id") String authorId) {
        List<BlogEntity> blogList;

        try {
            blogList = blogService.getAllBlogsOfAuthor(authorId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(blogList).build();
    }

    @GET
    @Path("/{id}/tags")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTagsOfBlog(@PathParam("id") String blogId) {
        List<TagEntity> tagList;

        try {
            tagList = tagService.getAllTagsOfBlog(blogId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }

        return Response.ok(tagList).build();
    }

    @POST
    @Path("/{id}/tags")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTagsForBlog(@PathParam("id") String id, List<TagEntity> tagList) {
        Response response;

        try {
            if (blogService.createTagListForBlog(id, tagList)) {
                response = Response.ok("Insert Tag list for blog " + id + " successfully!").build();
            } else {
                response =  Response.status(Response.Status.EXPECTATION_FAILED)
                        .entity("Insert Tag list for blog " + id + " fail")
                        .build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
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
}
