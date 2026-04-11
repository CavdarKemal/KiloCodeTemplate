package com.verein.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "verein-verwaltung-sicherer-geheimer-schluessel-fuer-jwt-token-generierung-2026");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        jwtUtil.init();
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken("testuser", "USER");
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testExtractRole() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        String role = jwtUtil.extractRole(token);
        assertEquals("ADMIN", role);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken("testuser", "USER");
        assertTrue(jwtUtil.validateToken(token, "testuser"));
    }

    @Test
    void testValidateTokenWrongUsername() {
        String token = jwtUtil.generateToken("testuser", "USER");
        assertFalse(jwtUtil.validateToken(token, "wronguser"));
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtUtil.generateToken("testuser", "USER");
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here", "testuser"));
    }
}