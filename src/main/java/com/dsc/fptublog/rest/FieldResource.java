package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.CategoryEntity;
import com.dsc.fptublog.entity.FieldEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.service.interfaces.IBlogService;
import com.dsc.fptublog.service.interfaces.ICategoryService;
import com.dsc.fptublog.service.interfaces.IFieldService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/fields")
public class FieldResource {

    @Inject
    private IFieldService fieldService;

    @Inject
    private ICategoryService categoryService;

    @Inject
    private IBlogService blogService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFields() {
        List<FieldEntity> result;
        try {
            result = fieldService.getAllFields();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getField(@PathParam("id") String id) {
        FieldEntity result;
        try {
            result = fieldService.getFieldById(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryOfFields(@PathParam("id") String fieldId) {
        List<CategoryEntity> result;
        try {
            result = categoryService.getCategoriesOfField(fieldId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{field_id}/blogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogsOfFields(@PathParam("field_id") String fieldId,
                                     @QueryParam("limit") int limit, @QueryParam("page") int page) {
        List<BlogEntity> result;

        try {
            result = blogService.getBlogsOfFields(fieldId, limit, page);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/top")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopFields() {
        List<FieldEntity> result;

        try {
            result = fieldService.getTopFields();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(result).build();
    }

    @GET
    @Path("/{field_id}/lecturers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLecturers(@PathParam("field_id")String fieldId){
        List<LecturerEntity> lecturersList;
        try{
            lecturersList = fieldService.getLecturersByFieldId(fieldId);
            if(lecturersList == null){
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("No lecturers found").build();
            }else{
                return Response.ok(lecturersList).build();
            }
        }catch (SQLException ex){
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
    }
}
