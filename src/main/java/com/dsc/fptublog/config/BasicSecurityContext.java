package com.dsc.fptublog.config;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class BasicSecurityContext implements SecurityContext {

    private final String subject;
    private final String role;
    private final boolean secure;

    public BasicSecurityContext(String subject, String role, boolean secure) {
        this.subject = subject;
        this.role = role;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> this.subject;
    }

    @Override
    public boolean isUserInRole(String role) {
        return role.equals(this.role);
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}
