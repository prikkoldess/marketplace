package com.example.marketplace.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.marketplace.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("")
    private String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }

    public Long extractId(Claims claims) {
        return claims.get("id", Long.class);
    }

    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public boolean isTokenValid(Claims claims, UserDetails userDetails) {
        final String email = claims.getSubject();
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(claims);
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
