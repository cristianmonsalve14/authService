package cl.duoc.libroDigital.authService.service;

import cl.duoc.libroDigital.authService.model.User;
import java.util.List;
import java.util.Optional;

public interface AdminUserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    void deleteUser(Long id);
    User updateUserRoles(Long id, List<Object> roles);
}
