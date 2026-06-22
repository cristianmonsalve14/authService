package cl.duoc.libroDigital.authService.service;

import cl.duoc.libroDigital.authService.dto.LoginRequest;
import cl.duoc.libroDigital.authService.dto.RegisterRequest;
import cl.duoc.libroDigital.authService.dto.AuthResponse;
import cl.duoc.libroDigital.authService.dto.UserProfileDTO;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(String refreshToken);

    UserProfileDTO getProfile(String username);
}
