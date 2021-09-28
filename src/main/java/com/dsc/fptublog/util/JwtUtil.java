package com.dsc.fptublog.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.dsc.fptublog.entity.AccountEntity;
import com.dsc.fptublog.entity.LecturerEntity;
import com.dsc.fptublog.entity.StudentEntity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JwtUtil {

    // The expire time for JWT in minutes
    private static final long EXPIRATION_LIMIT_IN_MINUTES = 30;

    private static final String ISSUER = "fptu-blog";

    // This JwtUtil using HS256 (HMAC with SHA-256) algorithm to create signature
    private static final String SECRET_KEY = "Real Muthaphuckkin G's";
    private static final Algorithm SIGNATURE_ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    private JwtUtil() {
    }

    public static JWTVerifier getVerifier() {
        JWTVerifier verifier = JWT.require(SIGNATURE_ALGORITHM)
                .withIssuer(ISSUER)
                .build();
        return verifier;
    }

    public static DecodedJWT getDecodedJWT(String token) {
        JWTVerifier verifier = getVerifier();
        DecodedJWT jwt = verifier.verify(token);
        return jwt;
    }


    public static String createJWT(AccountEntity account) {
        // Get the current time
        long currentTimeInMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeInMillis);

        // The key is only valid for the next EXPIRATION_LIMIT_IN_MINUTES
        long expirationTimeInMillis = TimeUnit.MINUTES.toMillis(EXPIRATION_LIMIT_IN_MINUTES);
        Date expirationDate = new Date(currentTimeInMillis + expirationTimeInMillis);

        // Determine account role
        String role = "";
        if (account instanceof StudentEntity) {
            role = "student";
        } else if (account instanceof LecturerEntity) {
            role = "lecturer";
        }

        // Create and Sign a Token
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withSubject(account.getId())
                .withClaim("role", role)
                .sign(SIGNATURE_ALGORITHM);
        return token;
    }

    public static AccountEntity getAccountFromToken(String token) {
        DecodedJWT jwt = getDecodedJWT(token);

        AccountEntity account = null;
        String id = jwt.getSubject();

        String role = jwt.getClaim("role").asString();
        if ("student".equals(role)) {
            account = StudentEntity.builder().id(id).build();
        } else if ("lecturer".equals(role)) {
            account = LecturerEntity.builder().id(id).build();
        }

        return account;
    }

    private static Date getExpirationDateFromToken(String token) {
        DecodedJWT jwt = getDecodedJWT(token);
        return jwt.getExpiresAt();
    }

    public static Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
