package com.dsc.fptublog.rest;

import com.dsc.fptublog.config.Role;
import com.dsc.fptublog.entity.BlogEntity;
import com.dsc.fptublog.entity.BlogStatusEntity;
import com.dsc.fptublog.model.BlogRateModel;
import com.dsc.fptublog.model.VoteModel;
import com.dsc.fptublog.service.interfaces.IBlogService;
import com.dsc.fptublog.service.interfaces.IRateService;
import lombok.extern.log4j.Log4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Log4j
@Path("/blogs")
public class BlogResource {

    @Inject
    private IBlogService blogService;

    @Inject
    private IRateService rateService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogs(@QueryParam("limit") int limit, @QueryParam("page") int page,
                                @QueryParam("title") String title) {
        List<BlogEntity> blogList;
        try {
            if (title == null || title.isBlank()) {
                blogList = blogService.getAllBlogs(limit, page);
            } else {
                blogList = blogService.getAllBlogs(limit, page, title);
            }
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(blogList).build();
    }

    @GET
    @Path("/top_rate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopBlogs(@QueryParam("limit") int limit, @QueryParam("page") int page) {
        List<BlogEntity> blogList;

        try {
            blogList = blogService.getTopBlogs(limit, page);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(blogList).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlog(@PathParam("id") String id) {
        BlogEntity blog;
        try {
            blog = blogService.getById(id);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return Response.ok(blog).build();
    }

    @POST
    @RolesAllowed({Role.STUDENT, Role.LECTURER, Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBlog(@Context SecurityContext sc, BlogEntity newBlog) {
        String userId = sc.getUserPrincipal().getName();
        newBlog.setAuthorId(userId);

        Response response;
        try {
            newBlog = blogService.createBlog(newBlog);
            response = Response.ok(newBlog).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBlog(@Context SecurityContext sc, @PathParam("id") String id, BlogEntity updatedBlog) {
        Response response;

        updatedBlog.setId(id);
        String userId = sc.getUserPrincipal().getName();
        BlogEntity result = null;
        try {
            result = blogService.updateBlog(userId, updatedBlog);
            if (result == null) {
                response = Response.status(Response.Status.EXPECTATION_FAILED).build();
            } else {
                response = Response.ok(result).build();
            }
        } catch (Exception ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @DELETE
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOwnBlog(@Context SecurityContext sc, @PathParam("id") String blogId) {
        String userId = sc.getUserPrincipal().getName();

        Response response;

        try {
            boolean result = blogService.deleteBlogOfAuthor(userId, blogId);
            if (result) {
                response = Response.ok("Delete blog successfully!").build();
            } else {
                response = Response.status(Response.Status.EXPECTATION_FAILED).entity("Delete blog fail").build();
            }
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @POST
    @Path("/{id}/undo_delete")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response undoDelete(@Context SecurityContext sc, @PathParam("id") String id) {
        String userId = sc.getUserPrincipal().getName();

        try {
            if (blogService.undoPendingDeleted(userId, id)) {
                return Response.ok("Undo deleted successfully!").build();
            } else {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("Cannot undo delete this blog").build();
            }
        } catch (Exception ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
    }

    @GET
    @Path("/authors/{author_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnBlogs(@PathParam("author_id") String authorId,
                                @QueryParam("limit") int limit, @QueryParam("page") int page,
                                @QueryParam("sort_by") String sortByField, @QueryParam("order_by") String orderByType) {
        List<BlogEntity> blogList;

        try {
            blogList = blogService.getAllBlogsOfAuthor(authorId, limit, page, sortByField, orderByType);
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(blogList).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogStatus() {
        List<BlogStatusEntity> blogStatusList;

        try {
            blogStatusList = blogService.getAllBlogStatus();
        } catch (SQLException ex) {
            log.error(ex);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return Response.ok(blogStatusList).build();
    }

    @GET
    @Path("/status/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogStatus(@PathParam("id") String id) {
        Response response;

        try {
            BlogStatusEntity blogStatus = blogService.getBlogStatus(id);
            response = Response.ok(blogStatus).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/{blog_id}/rates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlogsRates(@PathParam("blog_id") String blogId) {
        Response response;

        try {
            BlogRateModel blogRateModel = rateService.getRateOfBlog(blogId);
            response = Response.ok(blogRateModel).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @GET
    @Path("/{blog_id}/votes")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVotes(@Context SecurityContext sc, @PathParam("blog_id") String blogId) {
        String userId = sc.getUserPrincipal().getName();

        Response response;

        try {
            VoteModel voteModel = rateService.getVoteOfUserForBlog(userId, blogId);
            response = Response.ok(voteModel).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @POST
    @Path("/{blog_id}/votes")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVote(@Context SecurityContext sc, @PathParam("blog_id") String blogId, VoteModel voteModel) {
        String userId = sc.getUserPrincipal().getName();

        Response response;

        try {
            rateService.addVoteForBlog(userId, blogId, voteModel.getStar());
            BlogRateModel blogRateModel = rateService.getRateOfBlog(blogId);
            response = Response.ok(blogRateModel).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }

    @DELETE
    @Path("/{blog_id}/votes")
    @RolesAllowed({Role.STUDENT, Role.LECTURER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVote(@Context SecurityContext sc, @PathParam("blog_id") String blogId) {
        String userId = sc.getUserPrincipal().getName();

        Response response;

        try {
            rateService.deleteVoteForBlog(userId, blogId);
            BlogRateModel blogRateModel = rateService.getRateOfBlog(blogId);
            response = Response.ok(blogRateModel).build();
        } catch (SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }

        return response;
    }
}
