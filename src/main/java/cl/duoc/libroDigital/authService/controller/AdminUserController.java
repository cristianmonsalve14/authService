package cl.duoc.libroDigital.authService.controller;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.dto.UserResponse;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.service.AdminUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateAdminUserRequest request) {
        return ResponseEntity.ok(convertToResponse(adminUserService.createUser(request)));
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return adminUserService.getAllUsers().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(convertToResponse(adminUserService.getUserById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
            @PathVariable("id") Long id,
            @RequestBody List<String> roles) {
        return ResponseEntity.ok(convertToResponse(adminUserService.updateUserRoles(id, roles)));
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        response.setRoles(
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
        );
        return response;
    }
}
