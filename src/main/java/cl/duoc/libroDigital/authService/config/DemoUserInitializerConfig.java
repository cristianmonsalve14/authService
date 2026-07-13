package cl.duoc.libroDigital.authService.config;

import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.model.User;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import cl.duoc.libroDigital.authService.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Usuarios de arranque para desarrollo y primera instalación.
 * Solo crea cuentas si aún no existen; el admin puede provisionar accesos desde la app.
 */
@Configuration
public class DemoUserInitializerConfig {

    @Bean
    public CommandLineRunner initDemoUsers(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Role superAdminRole = roleRepository.findByName("SUPER_ADMINISTRADOR")
                    .orElseThrow(() -> new IllegalStateException("Rol SUPER_ADMINISTRADOR no encontrado"));
            Role adminLegacyRole = roleRepository.findByName("ADMINISTRADOR")
                    .orElseThrow(() -> new IllegalStateException("Rol ADMINISTRADOR no encontrado"));
            Role administrativeRole = roleRepository.findByName("ADMINISTRATIVO")
                    .orElseThrow(() -> new IllegalStateException("Rol ADMINISTRATIVO no encontrado"));
            Role teacherRole = roleRepository.findByName("DOCENTE")
                    .orElseThrow(() -> new IllegalStateException("Rol DOCENTE no encontrado"));
            Role guardianRole = roleRepository.findByName("APODERADO")
                    .orElseThrow(() -> new IllegalStateException("Rol APODERADO no encontrado"));
            Role studentRole = roleRepository.findByName("ESTUDIANTE")
                    .orElseThrow(() -> new IllegalStateException("Rol ESTUDIANTE no encontrado"));

            ensureUser(userRepository, passwordEncoder, "admin_colegio",
                    "admin@librodigital.cl", "test1234", Set.of(superAdminRole));

            ensureUser(userRepository, passwordEncoder, "admin_oficina",
                    "oficina@librodigital.cl", "test1234", Set.of(administrativeRole));

            ensureUser(userRepository, passwordEncoder, "prof_castillo",
                    "prof.castillo@duoc.cl", "test1234", Set.of(teacherRole));

            ensureUser(userRepository, passwordEncoder, "apoderado_demo",
                    "apoderado@librodigital.cl", "test1234", Set.of(guardianRole));

            ensureUser(userRepository, passwordEncoder, "estudiante_demo",
                    "estudiante@librodigital.cl", "test1234", Set.of(studentRole));

            // Migrar admin demo legado → super admin
            userRepository.findByUsername("admin_colegio").ifPresent(user -> {
                boolean hasSuper = user.getRoles() != null && user.getRoles().stream()
                        .anyMatch(r -> "SUPER_ADMINISTRADOR".equals(r.getName()));
                if (!hasSuper) {
                    user.setRoles(Set.of(superAdminRole));
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                }
            });

            userRepository.findByUsername("postman_test").ifPresent(user -> {
                if (user.getRoles() == null || user.getRoles().stream()
                        .noneMatch(r -> "SUPER_ADMINISTRADOR".equals(r.getName())
                                || "ADMINISTRADOR".equals(r.getName()))) {
                    user.setRoles(Set.of(adminLegacyRole));
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                }
            });
        };
    }

    private void ensureUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String email,
            String rawPassword,
            Set<Role> roles) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
