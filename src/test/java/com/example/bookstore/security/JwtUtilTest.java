package com.example.bookstore.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.List;
import java.util.Base64;



class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "redisTemplate", redisTemplate);
    
        // 確保 Redis ValueOperations 不為 null
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    
        // 產生符合 256-bit 要求的 Base64 秘鑰
        String secureBase64Key = Base64.getEncoder()
                .encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secureBase64Key);
    }
    

    @Test
    void testGenerateTokenAndExtractUsername() {
        String token = jwtUtil.generateToken("testUser", List.of("ROLE_USER"));
        assertNotNull(token);

        String username = jwtUtil.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void testExtractRoles() {
        String token = jwtUtil.generateToken("testUser", List.of("ROLE_ADMIN", "ROLE_MANAGER"));
        List<String> roles = jwtUtil.extractRoles(token);

        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_MANAGER"));
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtUtil.generateToken("validUser", List.of("ROLE_USER"));
        boolean isValid = jwtUtil.validateToken(token, "validUser");

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String token = jwtUtil.generateToken("wrongUser", List.of("ROLE_USER"));
        boolean isValid = jwtUtil.validateToken(token, "anotherUser");

        assertFalse(isValid);
    }

    @Test
    void testBlacklistToken() {
        String token = jwtUtil.generateToken("testUser", List.of("ROLE_USER"));

        when(redisTemplate.hasKey(token)).thenReturn(false);
        jwtUtil.blacklistToken(token);
        when(redisTemplate.hasKey(token)).thenReturn(true);

        assertTrue(jwtUtil.isTokenBlacklisted(token));
    }

    @Test
    void testIsTokenBlacklisted() {
        String token = jwtUtil.generateToken("testUser", List.of("ROLE_USER"));
        when(redisTemplate.hasKey(token)).thenReturn(true);

        assertTrue(jwtUtil.isTokenBlacklisted(token));
    }
}

@SpringBootTest
class JwtUtilIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "Qm2Z6nH3cJ4lMlsdfgVnPz7oX9O3lQ8gV5KzB4T9L3Y="); // 設定 JWT 密鑰
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("integrationUser", List.of("ROLE_ADMIN"));
        assertNotNull(token);

        boolean isValid = jwtUtil.validateToken(token, "integrationUser");
        assertTrue(isValid);
    }

    @Test
    void testTokenBlacklistIntegration() {
        String token = jwtUtil.generateToken("blacklistUser", List.of("ROLE_USER"));
        assertNotNull(token);

        jwtUtil.blacklistToken(token);
        boolean isBlacklisted = jwtUtil.isTokenBlacklisted(token);

        assertTrue(isBlacklisted);
    }
}

