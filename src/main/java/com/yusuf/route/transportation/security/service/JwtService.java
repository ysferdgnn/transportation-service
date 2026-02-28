package com.yusuf.route.transportation.security.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    private final String issuer;
    private final byte[] secret;
    private final long ttlMinutes;

    public JwtService(String issuer, String secret, long ttlMinutes) {
        this.issuer = Objects.requireNonNull(issuer);
        this.secret = Objects.requireNonNull(secret).getBytes(StandardCharsets.UTF_8);
        this.ttlMinutes = ttlMinutes;
        if (this.secret.length < 32) throw new IllegalArgumentException("HS256 secret must be at least 32 bytes.");
    }

    public String generateAccessToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlMinutes * 60);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(subject)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", roles == null ? List.of() : roles)
                .build();

        try {
            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(),
                    claims
            );
            jwt.sign(new MACSigner(secret));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("JWT signing failed", e);
        }
    }

    public JWTClaimsSet verifyAndGetClaims(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!jwt.verify(new MACVerifier(secret))) throw new SecurityException("Invalid signature");

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (!issuer.equals(claims.getIssuer())) throw new SecurityException("Invalid issuer");

            Date exp = claims.getExpirationTime();
            if (exp == null || exp.before(new Date())) throw new SecurityException("Token expired");

            if (claims.getSubject() == null || claims.getSubject().isBlank())
                throw new SecurityException("Missing subject");

            return claims;
        } catch (ParseException e) {
            throw new SecurityException("Invalid token format", e);
        } catch (JOSEException e) {
            throw new SecurityException("Token verification error", e);
        }
    }
}