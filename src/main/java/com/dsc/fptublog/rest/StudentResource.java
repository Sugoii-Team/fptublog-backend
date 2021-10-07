package com.dsc.fptublog.rest;


import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.StudentEntity;
import com.dsc.fptublog.service.interfaces.IStudentService;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Log4j
@Path("/students")
public class StudentResource {

    @Inject
    private IStudentService studentService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudent(@PathParam("id")String id){
        StudentEntity student;
        try {
            student = studentService.getStudent(id);
        }catch (SQLException ex){
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
    public Response updateStudent(@Context SecurityContext sc, StudentEntity student){
        StudentEntity updatedStudent;
        try {
            // get ID of current student is login
            String currentStudentId = sc.getUserPrincipal().getName();
            if(!student.getId().equals(currentStudentId)){
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("Update Wrong Student Profile").build();
            }
            updatedStudent = studentService.updateStudent(student);
        }catch (SQLException ex){
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(updatedStudent).build();
    }
}
