package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.CategoryEntity;
import com.dsc.fptublog.service.interfaces.ICategoryService;
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
@Path("/categories")
public class CategoryResource {

    @Inject
    private ICategoryService categoryService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategory(@PathParam("id") String id) {
        CategoryEntity category;

        try {
            category = categoryService.getCategory(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }

        return Response.ok(category).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories() {
        List<CategoryEntity> categoryList;

        try {
            categoryList = categoryService.getCategories();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("LOAD DATABASE FAILED").build();
        }

        return Response.ok(categoryList).build();
    }

    @GET
    @Path("/{category_id}/blogs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogsByCategoryId(@PathParam("category_id") String categoryId){
        List<BlogEntity> result;
        try{
            result = categoryService.getBlogsByCategoryId(categoryId);
            if(result == null){
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("No Blogs Found").build();
            }
        }catch (SQLException ex){
            return  Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(result).build();
    }

}
