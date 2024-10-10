package com.capstone.usa.auth.security;

import com.capstone.usa.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key jwtKey;
    private final long jwtExpirationInMs;

    public JwtUtil(
            @Value("${app.jwtSecret}")
            String jwtSecret,
            @Value("${app.jwtExpirationInMs}")
            long jwtExpirationInMs
    )
    {
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Claims claims = Jwts.claims();
        claims.put("userId", user.getPhoneNumber());
        claims.setSubject(user.getPhoneNumber());
        claims.setIssuedAt(new Date());
        claims.setExpiration(expiryDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(jwtKey)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }
}
