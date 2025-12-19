package com.example.mymemories.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final String jwtSecret;
    private final long jwtExpirationInMS;

    // Constructor injection for safer environment variable binding
    public JwtProvider(
            @Value("${app.jwtSecret}") String jwtSecret,
            @Value("${app.jwtExpirationInMS}") long jwtExpirationInMS
    ) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalArgumentException("JWT secret is missing! Please set app.jwtSecret in properties or environment variables.");
        }
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long for HS256 encryption.");
        }
        if (jwtExpirationInMS <= 0) {
            throw new IllegalArgumentException("JWT expiration must be greater than 0.");
        }

        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMS = jwtExpirationInMS;
    }

    // Convert secret string to SecretKey
    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT token
    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpirationInMS);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            // Optional: log exception
            return false;
        }
    }

    // Get claims from JWT token
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
