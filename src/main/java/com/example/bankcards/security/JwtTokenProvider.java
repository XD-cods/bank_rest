package com.example.bankcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        log.debug("generateToken. Generating JWT token for user: {}", userPrincipal.getId());

        String token = Jwts.builder()
            .setSubject(userPrincipal.getId().toString())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();

        log.debug("getSigningKey. JWT token generated successfully for user: {}", userPrincipal.getId());
        return token;
    }

    public UUID getUserIdFromToken(String token) {
        log.debug("getUserIdFromToken. Extracting user ID from JWT token");

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        UUID userId = UUID.fromString(claims.getSubject());
        log.debug("User ID extracted from token: {}", userId);
        return userId;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.error("validateToken. Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("validateToken. Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("validateToken. JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("validateToken. JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("validateToken. JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
