package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.service.interfaces.IAccountService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

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
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }

        return Response.ok(account).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("id") String id, AccountEntity account) {
        account.setId(id);
        boolean result;

        try {
            result = accountService.updateByAccount(account);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }

        if (result) {
            return Response.status(Response.Status.OK).entity("Update account " + id + " successfully").build();
        } else {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Update account " + id + " fail!").build();
        }
    }

}
