package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.TagEntity;
import com.dsc.fptublog.service.interfaces.ITagService;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
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

    @POST
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
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
}
