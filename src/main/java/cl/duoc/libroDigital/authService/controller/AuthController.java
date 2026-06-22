package cl.duoc.libroDigital.authService.controller;

import cl.duoc.libroDigital.authService.dto.LoginRequest;
import cl.duoc.libroDigital.authService.dto.RegisterRequest;
import cl.duoc.libroDigital.authService.dto.AuthResponse;
import cl.duoc.libroDigital.authService.dto.UserProfileDTO;
import cl.duoc.libroDigital.authService.service.AuthService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ✅ LOGIN (usa LoginRequest)
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // ✅ REGISTER (usa RegisterRequest)
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // ✅ REFRESH TOKEN 🔥
    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody AuthResponse request) {
        return authService.refreshToken(request.getRefreshToken());
    }

    @GetMapping("/me")
    public UserProfileDTO me(Authentication authentication) {
        return authService.getProfile(authentication.getName());
    }
}