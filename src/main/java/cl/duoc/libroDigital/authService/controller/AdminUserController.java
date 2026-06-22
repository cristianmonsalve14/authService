package cl.duoc.libroDigital.authService.controller;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.dto.UserResponse;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import cl.duoc.libroDigital.authService.service.AdminUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminUserService adminUserService;

    public AdminUserController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AdminUserService adminUserService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.adminUserService = adminUserService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateAdminUserRequest request) {
        User created = adminUserService.createUser(request);
        return ResponseEntity.ok(convertToResponse(created));
    }

    // ✅ Obtener todos los usuarios (AHORA CON DTO)
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // ✅ Obtener usuario por ID (CON DTO)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Actualizar roles (DEVUELVE DTO)
    @PutMapping("/{id}/roles")
    public ResponseEntity<?> updateUserRoles(
            @PathVariable("id") Long id,
            @RequestBody List<String> roles) {

        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 🚫 Bloquear rol ADMIN
        if (roles.stream().anyMatch(r -> r.equalsIgnoreCase("ADMINISTRADOR"))) {
            return ResponseEntity.badRequest()
                    .body("No se puede asignar el rol ADMINISTRADOR desde la aplicación.");
        }

        Set<Role> roleEntities = roles.stream()
                .map(roleName -> roleRepository.findByName(roleName).orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toSet());

        User user = userOpt.get();
        user.setRoles(roleEntities);

        userRepository.save(user);

        return ResponseEntity.ok(convertToResponse(user)); // ✅ DTO
    }

    // ✅ ✅ CONVERSOR ENTITY → DTO
    private UserResponse convertToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());

        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
        );

        return response;
    }
}
