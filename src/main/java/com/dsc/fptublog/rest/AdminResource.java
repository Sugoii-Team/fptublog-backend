package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.AdminEntity;
import com.dsc.fptublog.entity.CategoryEntity;
import com.dsc.fptublog.entity.FieldEntity;
import com.dsc.fptublog.service.interfaces.IAdminService;
import com.dsc.fptublog.service.interfaces.ICategoryService;
import com.dsc.fptublog.service.interfaces.IFieldService;
import com.dsc.fptublog.util.JwtUtil;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    @Inject
    private IFieldService fieldService;

    @Inject
    private ICategoryService categoryService;


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
    public Response updateRoleOrBan(@PathParam("id") String id, AccountEntity account) {
        account.setId(id);
        boolean result = false;
        try {
            //Ban an account
            if (account.getRole() == null) {
                result = adminService.banAccount(account);
                if (result) {
                    return Response.ok("Ban account successfully!!").build();
                }
            }
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

    @DELETE
    @Path("/blogs/{id}")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBlog(@PathParam("id")String blogId){
        try{
            boolean result = adminService.deleteBlog(blogId);
        }catch (SQLException ex){
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Blog does not exist").build();
        }
        return Response.ok("Delete Blog Successfully!!").build();
    }

    @PUT
    @Path("/accounts/unbanningaccount/{account_id}")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unbanAccount(@PathParam("account_id")String accountId){
        boolean result = false;
        try{
            result = adminService.unbanAccount(accountId);
        }catch (SQLException ex){
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        if (result){
            return Response.ok("Unban account Successfully!").build();
        }else{
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Unban Account Failed").build();
        }
    }

    @GET
    @Path("/fields")
    @RolesAllowed({Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFields(){
        Response response;
        List<FieldEntity> result = null;
        try{
            result = fieldService.getAllFields();
            response = Response.ok(result).build();
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @PUT
    @Path("/fields/{id}")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateField(@PathParam("id")String fieldId,FieldEntity updatedField){
        boolean result;
        Response response;
        FieldEntity existedField;
        try {
            existedField = fieldService.getFieldById(fieldId);
            if(existedField == null){//field does not exist
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Field does not exist").build();
                return response;
            }
            updatedField.setId(fieldId);
            result =  fieldService.updateField(updatedField);
            if(result){ // update successfully
                response = Response.ok("Update Field Successfully").build();
            }else { //update failed
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Update Field Failed").build();
            }
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @POST
    @Path("/fields")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createField(FieldEntity newField){
        Response response;
        FieldEntity result;
        try{
            result = fieldService.createField(newField);
            if(result == null){
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Create field failed").build();
                return response;
            }
            response = Response.ok(result).build();
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @DELETE
    @Path("/fields/{id}")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@PathParam("id")String fieldId){
        Response response;

        try{

        }catch (SQLException ex){

        }
        return response;
    }

    @GET
    @Path("/categories")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories(){
        Response response;
        List<CategoryEntity> result;
        try{
            result = categoryService.getCategories();
            response = Response.ok(result).build();
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

//    @PUT
//    @Path("/categories/{id}")
//    @RolesAllowed(Role.ADMIN)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateCategory(@PathParam("id")String categoryId,CategoryEntity updateCategory){
//        Response response;
//        boolean result;
//        CategoryEntity existedCategory;
//        try{
//            existedCategory = categoryService.getCategory(categoryId);
//            if(existedCategory == null){
//                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Category does not exist").build();
//                return response;
//            }
//            updateCategory.setId(existedCategory.getId());
//            result = categoryService.updateCategory(updateCategory);
//            if(result){
//                response = Response.ok("Update category successfully").build();
//            }else{
//                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Update category failed").build();
//                return response;
//            }
//        }catch (SQLException ex){
//            log.error(ex);
//            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
//        }
//        return response;
//    }

    @POST
    @Path("/categories")
    @RolesAllowed(Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCategory(CategoryEntity newCategory){
        Response response;
        CategoryEntity result;
        try{
            result = categoryService.createCategory(newCategory);
            if(result == null){
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Create category failed").build();
                return response;
            }
            response = Response.ok(result).build();
        }catch (SQLException ex){
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @DELETE
    @Path("/categories/{id}")
    @RolesAllowed(Role.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@PathParam("id")String categoryId){
        Response response;
        boolean result;
        try{

        }catch (SQLException ex){

        }
        return response;
    }
}
