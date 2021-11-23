package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AdminEntity;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.model.MessageModel;
import com.dsc.fptublog.service.interfaces.IAccountService;
import com.dsc.fptublog.service.interfaces.IAdminService;
import com.dsc.fptublog.service.interfaces.IBlogService;
import com.dsc.fptublog.util.JwtUtil;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/admin")
public class AdminResource {

    @Inject
    private IAdminService adminService;

    @Inject
    private IBlogService blogService;

    @Inject
    private IAccountService accountService;

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
            return Response.ok("{\"role\" : \"ADMIN\"}")
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
    public Response updateRole(@PathParam("id") String id, AccountEntity account) {
        account.setId(id);
        boolean result = false;
        try {
            //Ban an account
            /*if (account.getRole() == null) {
                result = adminService.banAccount(account);
                if (result) {
                    return Response.ok("Ban account successfully!!").build();
                }
            }*/
            //Update role cho 1 acc
            if (account.getRole() != null) {
                account = adminService.updateRole(account);
            } else {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("WRONG RESOURCE!!").build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok("Update Role Successfully").build();
    }

    @POST
    @Path("/accounts/banningstudent/{account_id}")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response banStudentAccount(@PathParam("account_id") String accountId, MessageModel messageModel) {
        boolean result;
        try {
            result = adminService.banAccount(accountId, messageModel.getMessage());
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        if (result) {
            return Response.ok("Ban student successfully!").build();
        } else {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Ban student failed!").build();
        }
    }


    @GET
    @Path("/accounts/bannedaccounts")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBannedAccounts() {
        List<AccountEntity> bannedAccounts;
        try {
            bannedAccounts = adminService.getAllBannedAccounts();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(bannedAccounts).build();
    }

    @DELETE
    @Path("/blogs/{id}")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBlog(@PathParam("id")String blogId){
        Response response;
        boolean isSuccessful = false;
        try{
            isSuccessful = adminService.deleteBlog(blogId);
            if(isSuccessful){
                response = Response.ok("Delete Blog Successfully").build();
            }else{
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Delete Blog Failed").build();
            }
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @PUT
    @Path("/accounts/unbanningaccount/{account_id}")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unbanAccount(@PathParam("account_id") String accountId) {
        boolean result = false;
        try {
            result = adminService.unbanAccount(accountId);
        } catch (SQLException ex) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        if (result) {
            return Response.ok("Unban account Successfully!").build();
        } else {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Unban Account Failed").build();
        }
    }

    @POST
    @Path("/blogs")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postBlog(BlogEntity newBlog){
        Response response;
        try{
            newBlog = adminService.createBlog(newBlog);
            response = Response.ok(newBlog).build();
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @PUT
    @Path("/blogs/{id}")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBlog(@PathParam("id")String blogId,BlogEntity updatedBlog){
        Response response;
        BlogEntity result = null;
        updatedBlog.setId(blogId);
        try{
            result = adminService.updateBlog(updatedBlog);
            if (result == null){
                response = Response.status(Response.Status.EXPECTATION_FAILED).build();
            }else{
                response = Response.ok(result).build();
            }
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @GET
    @Path("/blogs")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogs(@QueryParam("limit") int limit, @QueryParam("page") int page){
        Response response;
        List<BlogEntity> result;

        try{
            result = adminService.getAllBlogsOfAdmin(limit,page);
            if(result != null){
                response = Response.ok(result).build();
            }else{
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Get Blogs Failed").build();
            }
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }
}
