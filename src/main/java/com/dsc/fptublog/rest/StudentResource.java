package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.model.MessageModel;
import com.dsc.fptublog.model.Top30DaysStudentModel;
import com.dsc.fptublog.service.interfaces.IStudentService;

import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
@Path("/students")
public class StudentResource {

    @Inject
    private IStudentService studentService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudents() {
        List<StudentEntity> result;

        try {
            result = studentService.getAllStudents();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudent(@PathParam("id") String id) {
        StudentEntity student;
        try {
            student = studentService.getStudent(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(student).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({Role.STUDENT})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStudent(@Context SecurityContext sc, @PathParam("id") String id, StudentEntity student) {
        String userId = sc.getUserPrincipal().getName();
        student.setId(id);

        if (!userId.equals(id)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Update Wrong Student Profile")
                    .build();
        }

        StudentEntity updatedStudent;
        try {
            updatedStudent = studentService.updateStudent(student);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(updatedStudent).build();
    }

    @GET
    @Path("/top")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopStudentIn30Day() {
        List<Top30DaysStudentModel> result;

        try {
            result = studentService.getTopIn30Days();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(result).build();
    }

}
