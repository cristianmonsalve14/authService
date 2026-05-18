package cl.duoc.libroDigital.authService.config;

import cl.duoc.libroDigital.authService.model.Role;
import cl.duoc.libroDigital.authService.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RoleInitializerConfig {
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            List<String> roles = List.of("ADMINISTRADOR", "DOCENTE", "APODERADO", "ESTUDIANTE");
            for (String roleName : roles) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role role = new Role();
                            role.setName(roleName);
                            return roleRepository.save(role);
                        });
            }
        };
    }
}
