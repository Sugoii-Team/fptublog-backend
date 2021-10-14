package com.dsc.fptublog.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.dsc.fptublog.config.Role;
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
    private static final JWTVerifier VERIFIER =  JWT.require(SIGNATURE_ALGORITHM).withIssuer(ISSUER).build();

    private JwtUtil() {
    }

    public static JWTVerifier getVerifier() {
        return VERIFIER;
    }

    public static DecodedJWT getDecodedJWT(String token) {
        return VERIFIER.verify(token);
    }

    public static String createJWT(String subject, String role) {
        // Get the current time
        long currentTimeInMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeInMillis);

        // The key is only valid for the next EXPIRATION_LIMIT_IN_MINUTES
        long expirationTimeInMillis = TimeUnit.MILLISECONDS.convert(EXPIRATION_LIMIT_IN_MINUTES, TimeUnit.MINUTES);
        System.out.println(expirationTimeInMillis);
        Date expirationDate = new Date(currentTimeInMillis + expirationTimeInMillis);

        // Create and Sign a Token
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withSubject(subject)
                .withClaim("role", role)
                .sign(SIGNATURE_ALGORITHM);
    }

    private static Date getExpirationDateFromToken(String token) {
        DecodedJWT jwt = getDecodedJWT(token);
        return jwt.getExpiresAt();
    }

    public static boolean isTokenExpired(String token) {
        Date expiredDate = getExpirationDateFromToken(token);
        return expiredDate.before(new Date());
    }

    public static String getTokenSubject(String token) {
        DecodedJWT jwt = getDecodedJWT(token);
        return jwt.getSubject();
    }

    public static String getTokenRole(String token) {
        DecodedJWT jwt = getDecodedJWT(token);
        return jwt.getClaim("role").asString();
    }
}
