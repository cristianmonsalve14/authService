package cl.duoc.libroDigital.authService.service;

import cl.duoc.libroDigital.authService.dto.CreateAdminUserRequest;
import cl.duoc.libroDigital.authService.model.User;

public interface AdminUserService {
    User createUser(CreateAdminUserRequest request);
}
