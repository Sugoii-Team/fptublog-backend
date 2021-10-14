package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.service.interfaces.IStudentService;
import com.dsc.fptublog.service.interfaces.IBlogService;

import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/students")
public class StudentResource {

    @Inject
    private IStudentService studentService;

    @Inject
    private IBlogService blogService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudent(@PathParam("id") String id) {
        StudentEntity student;
        try {
            student = studentService.getStudent(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(student).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({Role.STUDENT})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStudent(@Context SecurityContext sc, StudentEntity student) {
        StudentEntity updatedStudent;
        try {
            // get ID of current student is login
            String currentStudentId = sc.getUserPrincipal().getName();
            if (!student.getId().equals(currentStudentId)) {
                return Response.status(Response.Status.EXPECTATION_FAILED)
                        .entity("Update Wrong Student Profile")
                        .build();
            }
            updatedStudent = studentService.updateStudent(student);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(updatedStudent).build();
    }

    @GET
    @RolesAllowed(Role.STUDENT)
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

    @DELETE
    @RolesAllowed(Role.STUDENT)
    @Path("/{student_id}/blogs/{blog_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOwnBlog(@PathParam("student_id") String studentId, @PathParam("blog_id") String blogId) {
        Response response;

        try {
            BlogEntity deletedBlog = blogService.deleteBlogOfAuthor(studentId, blogId);
            if (deletedBlog != null) {
                response = Response.ok(deletedBlog).build();
            } else {
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Delete blog fail").build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }
}
