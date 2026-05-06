package cl.duoc.libroDigital.authService.service;

import cl.duoc.libroDigital.authService.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    User save(User user);
}