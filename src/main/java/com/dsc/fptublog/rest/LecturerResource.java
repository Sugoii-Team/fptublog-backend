package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.FieldEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.model.ReviewModel;
import com.dsc.fptublog.service.interfaces.IBlogService;
import com.dsc.fptublog.service.interfaces.IFieldService;
import com.dsc.fptublog.service.interfaces.ILecturerService;
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

    @Inject
    private ILecturerService lecturerService;

    @Inject
    private IFieldService fieldService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLecturers() {
        List<LecturerEntity> result;
        try {
            result = lecturerService.getAllLecturers();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLecturer(@PathParam("id") String id) {
        LecturerEntity result;
        try {
            result = lecturerService.getLecturer(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}/fields")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLecturerFields(@PathParam("id") String id) {
        List<FieldEntity> result;
        try {
            result = fieldService.getLecturerFields(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/fields")
    @RolesAllowed(Role.LECTURER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFieldForLecturer(@Context SecurityContext sc, @PathParam("id") String lecturerId,
                                        List<FieldEntity> fieldList) {
        System.out.println(sc.getUserPrincipal().getName());
        System.out.println(lecturerId);
        System.out.println(fieldList);

        if (!sc.getUserPrincipal().getName().equals(lecturerId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Response response;

        try {
            if (fieldService.addLecturerFields(lecturerId, fieldList)) {
                response = Response.ok("Update fields successfully!").build();
            } else {
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Update fields fail!").build();
            }

        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

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
                                              @PathParam("blog_id") String blogId, ReviewModel reviewModel) {
        if (!sc.getUserPrincipal().getName().equals(lecturerId)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        boolean result;

        try {
            result = blogService.updateReviewStatus(reviewModel, lecturerId, blogId);
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

    @PUT
    @Path("/{lecturer_id}/banningstudent/{student_id}")
    @RolesAllowed(Role.LECTURER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response banStudentAccount(@Context SecurityContext sc,@PathParam("lecturer_id") String lecturerId, @PathParam("student_id") String studentId){
        if(!sc.getUserPrincipal().getName().equals(lecturerId)){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        boolean result;
        try{
            result = lecturerService.banStudent(studentId);
        }catch (SQLException ex){
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        if(result){
            return Response.ok("Ban student successfully!").build();
        }else {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Ban student failed!").build();
        }
    }

    @PUT
    @Path("/{lecturer_id}/unbanningstudent/{student_id}")
    @RolesAllowed(Role.LECTURER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unbanStudentAccount(@Context SecurityContext sc,@PathParam("lecturer_id") String lecturerId, @PathParam("student_id") String studentId){
        if(!sc.getUserPrincipal().getName().equals(lecturerId)){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        boolean result;
        try{
            result = lecturerService.unbanStudent(studentId);
        }catch (SQLException ex){
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        if (result){
            return Response.ok("Unban Student Successfully!!").build();
        }else{
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Unban Student Failed!!").build();
        }
    }
}
