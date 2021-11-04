package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.service.interfaces.IAccountService;
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
@Path("/accounts")
public class AccountResource {

    @Inject
    private IAccountService accountService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        AccountEntity account;

        try {
            account = accountService.getById(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(account).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(@Context SecurityContext sc, @PathParam("id") String id, AccountEntity account) {
        if (!sc.getUserPrincipal().getName().equals(id)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        account.setId(id);
        boolean result;

        Response response;

        try {
            result = accountService.updateByAccount(account);
            if (result) {
                response = Response.status(Response.Status.OK).entity("Update account " + id + " successfully").build();
            } else {
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Update account " + id + " fail!").build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/banned")
    @RolesAllowed({Role.LECTURER, Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBannedAccount() {

        List<AccountEntity> result;

        try {
            result = accountService.getBannedAccounts();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(result).build();
    }

}
