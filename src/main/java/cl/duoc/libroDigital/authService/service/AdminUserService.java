package cl.duoc.libroDigital.authService.service;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.model.User;

import java.util.List;

public interface AdminUserService {
    User createUser(CreateAdminUserRequest request);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUser(Long id);

    User updateUserRoles(Long id, List<String> roles);
}
