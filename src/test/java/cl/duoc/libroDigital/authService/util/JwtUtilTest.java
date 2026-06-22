package cl.duoc.libroDigital.authService.util;

import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("librodigital2026SecretKeyForJWTTokenGenerationAndValidation12345");
    }

    @Test
    void generateAndExtractAccessToken() {
        Role role = new Role();
        role.setName("ADMINISTRADOR");
        User user = new User();
        user.setId(5L);
        user.setUsername("admin");
        user.setEmail("admin@colegio.cl");
        user.setRoles(Set.of(role));

        String token = jwtUtil.generateToken(user);
        assertNotNull(token);

        assertEquals("admin", jwtUtil.extractUsername(token));
        Claims claims = jwtUtil.extractAllClaims(token);
        assertEquals(5L, claims.get("userId", Number.class).longValue());
        assertEquals("admin@colegio.cl", claims.get("email", String.class));
        assertEquals("ADMINISTRADOR", claims.get("roles", String.class));
    }

    @Test
    void generateRefreshToken_containsRefreshType() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@colegio.cl");

        String refresh = jwtUtil.generateRefreshToken(user);
        Claims claims = jwtUtil.extractAllClaims(refresh);
        assertEquals("refresh", claims.get("type", String.class));
        assertEquals("user1", claims.getSubject());
    }
}
