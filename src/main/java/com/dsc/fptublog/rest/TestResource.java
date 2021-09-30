package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
@PermitAll
public class TestResource {

    @GET
    @RolesAllowed({Role.LECTURER, Role.STUDENT})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTest() {
        return Response.ok("Hello bé Đạt <3 <3 <3").build();
    }
}
