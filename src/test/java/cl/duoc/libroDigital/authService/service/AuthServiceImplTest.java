package cl.duoc.libroDigital.authService.service;

import cl.duoc.libroDigital.authService.dto.AuthResponse;
import cl.duoc.libroDigital.authService.dto.LoginRequest;
import cl.duoc.libroDigital.authService.dto.RegisterRequest;
import cl.duoc.libroDigital.authService.dto.UserProfileDTO;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import cl.duoc.libroDigital.authService.service.impl.AuthServiceImpl;
import cl.duoc.libroDigital.authService.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("DOCENTE");
        user = new User();
        user.setId(1L);
        user.setUsername("prof_castillo");
        user.setEmail("prof@colegio.cl");
        user.setPassword("encoded");
        user.setRoles(Set.of(role));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("prof_castillo");
        request.setPassword("test1234");

        when(userRepository.findByUsername("prof_castillo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test1234", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(1L, response.getUserId());
        assertEquals("prof_castillo", response.getUsername());
        assertTrue(response.getRoles().contains("DOCENTE"));
    }

    @Test
    void login_invalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("prof_castillo");
        request.setPassword("wrong");

        when(userRepository.findByUsername("prof_castillo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().contains("incorrectos"));
    }

    @Test
    void login_unknownUser() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("test1234");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void register_disabled() {
        assertThrows(RuntimeException.class, () -> authService.register(new RegisterRequest()));
    }

    @Test
    void refreshToken_success() {
        when(jwtUtil.extractUsername("refresh")).thenReturn("prof_castillo");
        when(userRepository.findByUsername("prof_castillo")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("new-access");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("new-refresh");

        AuthResponse response = authService.refreshToken("refresh");

        assertEquals("new-access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    @Test
    void getProfile_success() {
        when(userRepository.findByUsername("prof_castillo")).thenReturn(Optional.of(user));

        UserProfileDTO profile = authService.getProfile("prof_castillo");

        assertEquals(1L, profile.getUserId());
        assertEquals("prof@colegio.cl", profile.getEmail());
        assertTrue(profile.getRoles().contains("DOCENTE"));
    }
}
