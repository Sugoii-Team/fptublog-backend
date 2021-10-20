package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AdminEntity;
import com.dsc.fptublog.service.interfaces.IAdminService;
import com.dsc.fptublog.util.JwtUtil;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/admin")
public class AdminResource {

    @Inject
    private IAdminService adminService;


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdminAuthentication(AdminEntity admin) {
        boolean result;

        try {
            result = adminService.getAuthentication(admin);
        } catch (SQLException | NoSuchAlgorithmException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        if (result) {
            String token = JwtUtil.createJWT(admin.getUsername(), Role.ADMIN);
            return Response.ok("Admin logins successfully!")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Invalid username or password")
                    .build();
        }
    }

    @GET
    @Path("/accounts")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        List<AccountEntity> accountList;
        try {
            accountList = adminService.getAllAccounts();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(accountList).build();
    }

    @DELETE
    @Path("/accounts/{id}")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@PathParam("id") String accountId) {
        try {
            adminService.deleteAccount(accountId);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok("Delete Account successful").build();
    }

    @PUT
    @Path("/accounts")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(AccountEntity account) {
        try {
            account = adminService.updateAccount(account);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(account).build();
    }

    @PUT
    @Path("/accounts/{id}")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoleOrBan(AccountEntity account) {
        try {
            //Ban an account
            if (account.getRole() == null) {
                boolean result = adminService.banAccount(account);
                if (result) {
                    return Response.ok("Ban account successfully!!").build();
                }
            }
            //Update role cho 1 acc
            if (account.getRole() != null) {
                account = adminService.updateAccount(account);
            } else {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("WRONG RESOURCE!!").build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok("Update Role Successfully").build();
    }


    @GET
    @Path("/accounts/bannedaccounts")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBannedAccounts(){
        List<AccountEntity> bannedAccounts;
        try{
            bannedAccounts = adminService.getAllBannedAccounts();
        }catch (SQLException ex){
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(bannedAccounts).build();
    }
}
