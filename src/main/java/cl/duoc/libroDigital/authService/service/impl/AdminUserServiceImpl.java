package cl.duoc.libroDigital.authService.service.impl;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import cl.duoc.libroDigital.authService.service.AdminUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("DOCENTE", "APODERADO", "ESTUDIANTE");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateAdminUserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        String roleName = request.getRole().trim().toUpperCase(Locale.ROOT);
        if ("ADMINISTRADOR".equals(roleName)) {
            throw new IllegalArgumentException("No se puede crear un usuario ADMINISTRADOR desde la aplicación");
        }
        if (!ALLOWED_ROLES.contains(roleName)) {
            throw new IllegalArgumentException("Rol no permitido: " + roleName);
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado en una cuenta de acceso");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}
