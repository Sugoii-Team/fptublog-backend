package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.TagEntity;
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
@Path("/tags")
public class TagResource {

    @Inject
    private ITagService tagService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTags() {
        List<TagEntity> tagList;

        try {
            tagList = tagService.getTags();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }

        return Response.ok(tagList).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTag(@PathParam("id") String id) {
        TagEntity tag;

        try {
            tag = tagService.getTag(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }

        return Response.ok(tag).build();
    }

    @GET
    @Path("/top")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopTag(@QueryParam("limit") int limit, @QueryParam("page") int page) {
        List<TagEntity> result;

        try {
            result = tagService.getTopTag(limit, page);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(result).build();
    }

    @POST
    @RolesAllowed({Role.STUDENT, Role.LECTURER, Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTag(TagEntity newTag) {
        try {
            newTag = tagService.createTag(newTag);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(newTag).build();
    }

    @GET
    @Path("/blogs/{blog_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTagsOfBlog(@PathParam("blog_id") String blogId) {
        List<TagEntity> tagList;

        try {
            tagList = tagService.getAllTagsOfBlog(blogId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(tagList).build();
    }

    @POST
    @Path("/blogs/{blog_id}")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTagsForBlog(@PathParam("blog_id") String blogId, List<TagEntity> tagList) {
        Response response;

        try {
            tagList = tagService.createTagListForBlog(blogId, tagList);
            if (tagList != null) {
                response = Response.ok(tagList).build();
            } else {
                response =  Response.status(Response.Status.EXPECTATION_FAILED)
                        .entity("Insert Tag list for blog " + blogId + " fail")
                        .build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @PUT
    @Path("/blogs/{blog_id}")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTagsForBlog(@Context SecurityContext sc,
                                      @PathParam("blog_id") String blogId, List<TagEntity> tagList) {
        String userId = sc.getUserPrincipal().getName();

        Response response;

        try {
            tagList = tagService.updateTagListForBlog(userId, blogId, tagList);
            if (tagList != null) {
                response = Response.ok(tagList).build();
            } else {
                response = Response.status(Response.Status.EXPECTATION_FAILED)
                        .entity("Update Tag list for blog " + blogId + " fail")
                        .build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/{tag_id}/blogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogsOfTag(@PathParam("tag_id") String tagId,
                                  @QueryParam("limit") int limit, @QueryParam("page") int page) {
        Response response;

        try {
            List<BlogEntity> result = tagService.getBlogsOfTag(tagId, limit, page);
            response = Response.ok(result).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;

    }
}
