package com.example.bookstore.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 小時

    private static String generateSecureKey() {
        // 產生 256-bit 的安全密鑰
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
    }

    // 產生 Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 設定使用者名稱
                .setIssuedAt(new Date()) // 設定簽發時間
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 設定過期時間
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 用密鑰加密
                .compact();
    }

    // 解析 Token：提取使用者名稱
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 從 Token 提取特定的資訊（例如 username、過期時間 等）
    // 解析 Token，確保它是合法的，並使用簽名金鑰驗證 Token 沒有被篡改
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // 驗證 Token
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    // 檢查 Token 是否已經過期
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
