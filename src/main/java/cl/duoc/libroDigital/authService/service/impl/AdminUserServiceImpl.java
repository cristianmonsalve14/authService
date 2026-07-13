package cl.duoc.libroDigital.authService.service.impl;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.exception.BadRequestException;
import cl.duoc.libroDigital.authService.exception.ConflictException;
import cl.duoc.libroDigital.authService.exception.NotFoundException;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import cl.duoc.libroDigital.authService.service.AdminUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("DOCENTE", "APODERADO", "ESTUDIANTE", "ADMINISTRATIVO");
    private static final Set<String> BLOCKED_ROLES = Set.of("ADMINISTRADOR", "SUPER_ADMINISTRADOR");

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
            throw new BadRequestException("El nombre de usuario es obligatorio");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new BadRequestException("La contraseña debe tener al menos 8 caracteres");
        }
        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new BadRequestException("El rol es obligatorio");
        }

        String roleName = request.getRole().trim().toUpperCase(Locale.ROOT);
        if (BLOCKED_ROLES.contains(roleName)) {
            throw new BadRequestException("No se puede crear un usuario " + roleName + " desde la aplicación");
        }
        if (!ALLOWED_ROLES.contains(roleName)) {
            throw new BadRequestException("Rol no permitido: " + roleName);
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new ConflictException("El nombre de usuario ya está en uso");
        }
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException("El email ya está registrado en una cuenta de acceso");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado: " + roleName));

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

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Usuario no encontrado: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User updateUserRoles(Long id, List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new BadRequestException("Debe indicar al menos un rol");
        }
        if (roles.stream().anyMatch(r -> r != null && BLOCKED_ROLES.contains(r.trim().toUpperCase(Locale.ROOT)))) {
            throw new BadRequestException("No se puede asignar roles de super administración desde la aplicación.");
        }

        User user = getUserById(id);
        Set<Role> roleEntities = roles.stream()
                .map(roleName -> roleName.trim().toUpperCase(Locale.ROOT))
                .filter(ALLOWED_ROLES::contains)
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new NotFoundException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        if (roleEntities.isEmpty()) {
            throw new BadRequestException("Ningún rol válido para asignar");
        }

        user.setRoles(roleEntities);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
