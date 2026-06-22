package cl.duoc.libroDigital.authService.controller;

import cl.duoc.libroDigital.authService.dto.AuthResponse;
import cl.duoc.libroDigital.authService.dto.LoginRequest;
import cl.duoc.libroDigital.authService.dto.RegisterRequest;
import cl.duoc.libroDigital.authService.dto.UserProfileDTO;
import cl.duoc.libroDigital.authService.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_delegatesToAuthService() {
        LoginRequest request = new LoginRequest();
        AuthResponse expected = new AuthResponse();
        when(authService.login(request)).thenReturn(expected);

        AuthResponse response = authController.login(request);

        assertSame(expected, response);
        verify(authService).login(request);
    }

    @Test
    void register_delegatesToAuthService() {
        RegisterRequest request = new RegisterRequest();
        AuthResponse expected = new AuthResponse();
        when(authService.register(request)).thenReturn(expected);

        AuthResponse response = authController.register(request);

        assertSame(expected, response);
        verify(authService).register(request);
    }

    @Test
    void refreshToken_delegatesToAuthServiceUsingRefreshTokenValue() {
        AuthResponse request = new AuthResponse();
        request.setRefreshToken("refresh-token");
        AuthResponse expected = new AuthResponse();
        when(authService.refreshToken("refresh-token")).thenReturn(expected);

        AuthResponse response = authController.refreshToken(request);

        assertSame(expected, response);
        verify(authService).refreshToken("refresh-token");
    }

    @Test
    void me_delegatesToProfileServiceUsingAuthenticationName() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("prof_castillo");

        UserProfileDTO expected = new UserProfileDTO();
        expected.setUsername("prof_castillo");
        when(authService.getProfile("prof_castillo")).thenReturn(expected);

        UserProfileDTO response = authController.me(authentication);

        assertSame(expected, response);
        verify(authService).getProfile("prof_castillo");
    }
}
