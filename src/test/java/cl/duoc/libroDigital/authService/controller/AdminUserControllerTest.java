package cl.duoc.libroDigital.authService.controller;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.dto.UserResponse;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import cl.duoc.libroDigital.authService.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController adminUserController;

    @Test
    void createUser_returnsCreatedUserAsDto() {
        CreateAdminUserRequest request = new CreateAdminUserRequest();
        request.setUsername("jperez");
        request.setEmail("jperez@colegio.cl");
        request.setPassword("password123");
        request.setRole("DOCENTE");

        User created = user(10L, "jperez", "jperez@colegio.cl", "DOCENTE");
        when(adminUserService.createUser(request)).thenReturn(created);

        ResponseEntity<UserResponse> response = adminUserController.createUser(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        assertEquals("jperez", response.getBody().getUsername());
        assertEquals("jperez@colegio.cl", response.getBody().getEmail());
        assertEquals(Set.of("DOCENTE"), response.getBody().getRoles());
        verify(adminUserService).createUser(request);
    }

    @Test
    void getAllUsers_mapsAllUsersToDto() {
        User userOne = user(1L, "profesor", "profesor@colegio.cl", "DOCENTE");
        User userTwo = user(2L, "apoderado", "apoderado@colegio.cl", "APODERADO");
        when(userRepository.findAll()).thenReturn(List.of(userOne, userTwo));

        List<UserResponse> response = adminUserController.getAllUsers();

        assertEquals(2, response.size());
        assertEquals(Set.of("DOCENTE"), response.get(0).getRoles());
        assertEquals(Set.of("APODERADO"), response.get(1).getRoles());
        verify(userRepository).findAll();
    }

    @Test
    void getUser_returnsUserWhenFound() {
        User user = user(3L, "coordinador", "coord@colegio.cl", "COORDINADOR");
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        ResponseEntity<UserResponse> response = adminUserController.getUser(3L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("coordinador", response.getBody().getUsername());
        assertEquals(Set.of("COORDINADOR"), response.getBody().getRoles());
    }

    @Test
    void getUser_returnsNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> response = adminUserController.getUser(99L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void deleteUser_deletesUserWhenPresent() {
        when(userRepository.existsById(5L)).thenReturn(true);

        ResponseEntity<Void> response = adminUserController.deleteUser(5L);

        assertEquals(204, response.getStatusCode().value());
        verify(userRepository).deleteById(5L);
    }

    @Test
    void deleteUser_returnsNotFoundWhenUserIsMissing() {
        when(userRepository.existsById(50L)).thenReturn(false);

        ResponseEntity<Void> response = adminUserController.deleteUser(50L);

        assertEquals(404, response.getStatusCode().value());
        verify(userRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void updateUserRoles_blocksAdministradorRole() {
        User existing = user(7L, "user", "user@colegio.cl", "DOCENTE");
        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = adminUserController.updateUserRoles(7L, List.of("DOCENTE", "ADMINISTRADOR"));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("No se puede asignar el rol ADMINISTRADOR desde la aplicación.", response.getBody());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository);
    }

    @Test
    void updateUserRoles_updatesRoleSetAndReturnsDto() {
        User existing = user(8L, "maria", "maria@colegio.cl", "APODERADO");
        Role docente = role(1L, "DOCENTE");
        Role inspector = role(2L, "INSPECTOR");

        when(userRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(roleRepository.findByName("DOCENTE")).thenReturn(Optional.of(docente));
        when(roleRepository.findByName("INSPECTOR")).thenReturn(Optional.of(inspector));
        when(userRepository.save(existing)).thenReturn(existing);

        ResponseEntity<?> response = adminUserController.updateUserRoles(8L, List.of("DOCENTE", "INSPECTOR"));

        assertEquals(200, response.getStatusCode().value());
        assertInstanceOf(UserResponse.class, response.getBody());
        UserResponse body = (UserResponse) response.getBody();
        assertNotNull(body);
        assertEquals(Set.of("DOCENTE", "INSPECTOR"), body.getRoles());
        verify(userRepository).save(existing);
    }

    private static User user(Long id, String username, String email, String... roleNames) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRoles(Arrays.stream(roleNames)
                .map(roleName -> role(null, roleName))
                .collect(Collectors.toCollection(HashSet::new)));
        return user;
    }

    private static Role role(Long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        return role;
    }
}
