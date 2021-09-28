package com.dsc.fptublog.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private Response.StatusType getStatusType(Throwable throwable) {
        if (throwable instanceof WebApplicationException) {
            return ((WebApplicationException) throwable).getResponse().getStatusInfo();
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public Response toResponse(Throwable throwable) {
        return Response.status(getStatusType(throwable))
                .entity(throwable.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
