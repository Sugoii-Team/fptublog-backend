package com.dsc.fptublog.filter;

import com.dsc.fptublog.config.BasicSecurityContext;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.util.JwtUtil;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String REALM = "fptu-blog";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null
                && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        Response respone = Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                .entity("You cannot access this resource")
                .build();
        requestContext.abortWith(respone);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 1. Get Token Authorization from the header
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // 2. Validate the Authorization from the header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            return;
        }

        // 3. Extract the token from the Authorization header
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).strip();

        // 4. Validate the token
        if (JwtUtil.isTokenExpired(token)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // 5. Getting the User information from token
        AccountEntity account = JwtUtil.getAccountFromToken(token);

        // 6. Overriding the security context of the current request
        SecurityContext oldContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new BasicSecurityContext(account, oldContext.isSecure()));
    }
}
