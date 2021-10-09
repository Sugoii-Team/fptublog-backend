package com.dsc.fptublog.config;

import com.dsc.fptublog.entity.AccountEntity;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class BasicSecurityContext implements SecurityContext {

    private final AccountEntity account;
    private final boolean secure;

    public BasicSecurityContext(AccountEntity account, boolean secure) {
        this.account = account;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return account::getId;
    }

    @Override
    public boolean isUserInRole(String role) {
        return role.equals(account.getRole());
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}
