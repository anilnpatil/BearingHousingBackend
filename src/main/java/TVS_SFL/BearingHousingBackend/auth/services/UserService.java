package TVS_SFL.BearingHousingBackend.auth.services;

import TVS_SFL.BearingHousingBackend.auth.entities.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    boolean deleteUser(Long id);
}
