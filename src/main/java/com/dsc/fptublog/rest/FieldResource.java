package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.FieldEntity;
import com.dsc.fptublog.service.interfaces.IFieldService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/fields")
public class FieldResource {

    @Inject
    private IFieldService fieldService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFields() {

        try {
            List<FieldEntity> result = fieldService.getAllFields();
            return Response.ok(result).build();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
    }
}
