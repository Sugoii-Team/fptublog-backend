package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.AwardEntity;
import com.dsc.fptublog.entity.LecturerStudentAwardEntity;
import com.dsc.fptublog.service.interfaces.IAwardService;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
@Path("/awards")
public class AwardResource {

    @Inject
    private IAwardService awardService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAwards() {
        Response response;

        try {
            List<AwardEntity> awardList = awardService.getAwards();
            response = Response.ok(awardList).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/{award_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAward(@PathParam("award_id") String id) {
        Response response;

        try {
            AwardEntity award = awardService.getAward(id);
            response = Response.ok(award).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/students/{student_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAwardOfStudent(@PathParam("student_id") String studentId) {
        Response response;

        try {
            List<LecturerStudentAwardEntity> studentAwardList = awardService.getAllAwardOfStudent(studentId);
            response = Response.ok(studentAwardList).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @POST
    @Path("/students/{student_id}")
    @RolesAllowed(Role.LECTURER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response giveAward(@Context SecurityContext sc, @PathParam("student_id") String studentId,
                              LecturerStudentAwardEntity lecturerStudentAward) {
        String lecturerId = sc.getUserPrincipal().getName();
        lecturerStudentAward.setLecturerId(lecturerId);
        lecturerStudentAward.setStudentId(studentId);

        Response response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Give award fail!").build();

        try {
            if (awardService.giveAward(lecturerStudentAward)) {
                response = Response.ok("Give award successfully!").build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @DELETE
    @Path("/students/{student_id}/{id}")
    @RolesAllowed(Role.LECTURER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAwardOfStudent(@Context SecurityContext sc, @PathParam("student_id") String studentId,
                                         @PathParam("id") String id) {
        String lecturerId = sc.getUserPrincipal().getName();

        Response response;

        try {
            LecturerStudentAwardEntity deletedEntity = awardService.deleteAwardOfStudent(id, lecturerId, studentId);
            if (deletedEntity == null) {
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Delete fail!").build();
            } else {
                response = Response.ok(deletedEntity).build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

}
