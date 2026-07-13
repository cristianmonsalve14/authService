package cl.duoc.libroDigital.authService.controller;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.dto.UserResponse;
import cl.duoc.libroDigital.authService.exception.BadRequestException;
import cl.duoc.libroDigital.authService.exception.NotFoundException;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
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
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

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
        assertEquals(Set.of("DOCENTE"), response.getBody().getRoles());
        verify(adminUserService).createUser(request);
    }

    @Test
    void getAllUsers_mapsAllUsersToDto() {
        when(adminUserService.getAllUsers()).thenReturn(List.of(
                user(1L, "profesor", "profesor@colegio.cl", "DOCENTE"),
                user(2L, "apoderado", "apoderado@colegio.cl", "APODERADO")
        ));

        List<UserResponse> response = adminUserController.getAllUsers();

        assertEquals(2, response.size());
        verify(adminUserService).getAllUsers();
    }

    @Test
    void getUser_returnsUserWhenFound() {
        when(adminUserService.getUserById(3L))
                .thenReturn(user(3L, "coordinador", "coord@colegio.cl", "DOCENTE"));

        ResponseEntity<UserResponse> response = adminUserController.getUser(3L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("coordinador", response.getBody().getUsername());
    }

    @Test
    void getUser_propagatesNotFound() {
        when(adminUserService.getUserById(99L)).thenThrow(new NotFoundException("Usuario no encontrado: 99"));
        assertThrows(NotFoundException.class, () -> adminUserController.getUser(99L));
    }

    @Test
    void deleteUser_delegatesToService() {
        ResponseEntity<Void> response = adminUserController.deleteUser(5L);
        assertEquals(204, response.getStatusCode().value());
        verify(adminUserService).deleteUser(5L);
    }

    @Test
    void updateUserRoles_returnsDto() {
        when(adminUserService.updateUserRoles(8L, List.of("DOCENTE")))
                .thenReturn(user(8L, "maria", "maria@colegio.cl", "DOCENTE"));

        ResponseEntity<UserResponse> response =
                adminUserController.updateUserRoles(8L, List.of("DOCENTE"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Set.of("DOCENTE"), response.getBody().getRoles());
    }

    @Test
    void updateUserRoles_propagatesBadRequest() {
        when(adminUserService.updateUserRoles(7L, List.of("ADMINISTRADOR")))
                .thenThrow(new BadRequestException("No se puede asignar el rol ADMINISTRADOR desde la aplicación."));
        assertThrows(BadRequestException.class,
                () -> adminUserController.updateUserRoles(7L, List.of("ADMINISTRADOR")));
    }

    private static User user(Long id, String username, String email, String... roleNames) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRoles(Arrays.stream(roleNames)
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                })
                .collect(Collectors.toCollection(HashSet::new)));
        return user;
    }
}
