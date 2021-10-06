package com.dsc.fptublog.rest;


import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AdminEntity;
import com.dsc.fptublog.service.interfaces.IAdminService;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/admin")
public class AdminResource {

    @Inject
    private IAdminService adminService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(AdminEntity admin){
            return  null;
    }

    @GET
    @Path("/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts(){
        List<AccountEntity> accountList;
        try {
            accountList = adminService.getAllAccounts();
        }catch (SQLException ex){
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(accountList).build();
    }

    @DELETE
    @Path("/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(AccountEntity account){
        try {
            account = adminService.updateAccount(account);
        }catch (SQLException ex){
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }
        return Response.ok(account).build();
    }
}
