package com.example.bookstore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 小時

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object rolesObject = extractAllClaims(token).get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        List<String> roles = extractRoles(token);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public boolean validateToken(String token, String username) {
        return !isTokenBlacklisted(token) && extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    public void blacklistToken(String token) {
        redisTemplate.opsForValue().set("test_key", "working", 3600, TimeUnit.SECONDS);
        System.out.println("Test key stored in Redis.");

        System.out.println("=== Blacklisting Token ===");
        System.out.println("Token: " + token);
    
        long expiration = getTokenExpiration(token);
        System.out.println("Expiration time (ms): " + expiration);
    
        redisTemplate.opsForValue().set(token, "blacklisted", expiration, TimeUnit.MILLISECONDS);
        System.out.println("Token stored in Redis.");
    }
    

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }


    private long getTokenExpiration(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }
}
