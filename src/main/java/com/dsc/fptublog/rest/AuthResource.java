package com.dsc.fptublog.rest;

import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.service.interfaces.IAuthService;
import com.dsc.fptublog.util.JwtUtil;
import com.dsc.fptublog.util.ResourcesUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.log4j.Log4j;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collections;

@Log4j
@Path("/auth")
public class AuthResource {

    private static final String CLIENT_ID;

    static {
        // Load client_secret.json file
        String clientSecretFileLocation = ResourcesUtil.getAbsolutePath("client_secret.json");
        GoogleClientSecrets clientSecrets = null;
        try {
            clientSecrets = GoogleClientSecrets.load(
                    GsonFactory.getDefaultInstance(),
                    new FileReader(clientSecretFileLocation));
        } catch (IOException ex) {
            log.error(ex);
        }
        CLIENT_ID = clientSecrets.getDetails().getClientId();
    }

    @Inject
    private IAuthService authService;

    @POST
    @Path("/registration")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createRegistration(@FormParam("id_token") String idTokenString) {
        Response response;

        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                        .setAudience(Collections.singleton(CLIENT_ID))
                        .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                // get some gmail info from token
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String avatarUrl = (String) payload.get("picture");

                // call service to resolve this gmail info
                AccountEntity account = authService.createNewAccount(email, name, avatarUrl);
                if (account != null) {
                    String token = JwtUtil.createJWT(account.getId(), account.getRole());
                    response = Response.ok(account).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
                } else {
                    response = Response.status(Response.Status.NOT_ACCEPTABLE)
                            .entity("Email is not in FPT edu")
                            .build();
                }
            } else {
                response = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Invalid id_token").build();
            }
        } catch (GeneralSecurityException | IOException | SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        }
        return response;
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getAuthentication(@FormParam("id_token") String idTokenString) {
        Response response;

        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                        .setAudience(Collections.singleton(CLIENT_ID))
                        .build();
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                // get some gmail info from token
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                // call service to resolve this gmail info
                AccountEntity account = authService.getLogin(email);
                if (account != null) {
                    String token = JwtUtil.createJWT(account.getId(), account.getRole());
                    response = Response.ok(account)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .build();
                } else {
                    response = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Account is not existed").build();
                }
            } else {
                response = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Invalid id_token").build();
            }
        } catch (GeneralSecurityException | IOException | SQLException ex) {
            log.error(ex);
            response = Response.status(Response.Status.EXPECTATION_FAILED).entity(ex).build();
        } catch (Exception ex) {
            response = Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }

        return response;
    }
}
