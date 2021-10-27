package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.MajorEntity;
import com.dsc.fptublog.service.interfaces.IMajorService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/majors")
public class MajorResource {

    @Inject
    private IMajorService majorService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMajors() {
        List<MajorEntity> result;
        try {
            result = majorService.getAllMajor();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMajor(@PathParam("id") String id) {
        MajorEntity result;

        try {
            result = majorService.getMajor(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }
}
