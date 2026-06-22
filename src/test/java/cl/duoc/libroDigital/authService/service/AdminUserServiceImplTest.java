package cl.duoc.libroDigital.authService.service;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import cl.duoc.libroDigital.authService.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private CreateAdminUserRequest validRequest;
    private Role apoderadoRole;

    @BeforeEach
    void setUp() {
        validRequest = new CreateAdminUserRequest();
        validRequest.setUsername("jperez");
        validRequest.setEmail("jperez@colegio.cl");
        validRequest.setPassword("password123");
        validRequest.setRole("APODERADO");

        apoderadoRole = new Role();
        apoderadoRole.setId(3L);
        apoderadoRole.setName("APODERADO");
    }

    @Test
    void createUser_success() {
        when(userRepository.findByUsername("jperez")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("jperez@colegio.cl")).thenReturn(Optional.empty());
        when(roleRepository.findByName("APODERADO")).thenReturn(Optional.of(apoderadoRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        User created = adminUserService.createUser(validRequest);

        assertEquals(10L, created.getId());
        assertEquals("jperez", created.getUsername());
        assertEquals("encoded", created.getPassword());
        assertTrue(created.isEnabled());
        assertEquals(1, created.getRoles().size());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("jperez@colegio.cl", captor.getValue().getEmail());
    }

    @Test
    void createUser_rejectsBlankUsername() {
        validRequest.setUsername("  ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> adminUserService.createUser(validRequest));
        assertTrue(ex.getMessage().contains("usuario"));
    }

    @Test
    void createUser_rejectsShortPassword() {
        validRequest.setPassword("abc");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> adminUserService.createUser(validRequest));
        assertTrue(ex.getMessage().contains("contraseña"));
    }

    @Test
    void createUser_rejectsAdminRole() {
        validRequest.setRole("ADMINISTRADOR");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> adminUserService.createUser(validRequest));
        assertTrue(ex.getMessage().contains("ADMINISTRADOR"));
    }

    @Test
    void createUser_rejectsDuplicateUsername() {
        when(userRepository.findByUsername("jperez")).thenReturn(Optional.of(new User()));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> adminUserService.createUser(validRequest));
        assertTrue(ex.getMessage().contains("usuario"));
    }

    @Test
    void createUser_rejectsDuplicateEmail() {
        when(userRepository.findByUsername("jperez")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("jperez@colegio.cl")).thenReturn(Optional.of(new User()));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> adminUserService.createUser(validRequest));
        assertTrue(ex.getMessage().contains("email"));
    }
}
