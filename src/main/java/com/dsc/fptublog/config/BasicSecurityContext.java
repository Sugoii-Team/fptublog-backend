package com.dsc.fptublog.config;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.entity.StudentEntity;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class BasicSecurityContext implements SecurityContext {

    private AccountEntity account;
    private boolean secure;

    public BasicSecurityContext(AccountEntity account, boolean secure) {
        this.account = account;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> account.getId();
    }

    @Override
    public boolean isUserInRole(String role) {
        return account instanceof StudentEntity || account instanceof LecturerEntity;
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
