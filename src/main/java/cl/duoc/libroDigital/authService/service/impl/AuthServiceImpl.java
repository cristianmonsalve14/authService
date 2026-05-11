package cl.duoc.libroDigital.authService.service.impl;

import cl.duoc.libroDigital.authService.dto.LoginRequest;
import cl.duoc.libroDigital.authService.dto.RegisterRequest;
import cl.duoc.libroDigital.authService.dto.AuthResponse;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import cl.duoc.libroDigital.authService.service.AuthService;
import cl.duoc.libroDigital.authService.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Collections;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ LOGIN CORRECTO
    @Override
    public AuthResponse login(LoginRequest request) {

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        // ✅ GENERAR TOKENS
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    // ✅ REGISTER CORRECTO
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("USER");
                    return roleRepository.save(r);
                });

        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        // ✅ GENERAR TOKENS
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    // ✅ REFRESH TOKEN IMPLEMENTADO
    @Override
    public AuthResponse refreshToken(String refreshToken) {

        String username = jwtUtil.extractUsername(refreshToken);

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Token inválido");
        }

        User user = userOpt.get();

        // ✅ GENERAR NUEVO ACCESS TOKEN
        String newAccessToken = jwtUtil.generateToken(user);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshToken); // puedes renovar si quieres

        return response;
    }
}