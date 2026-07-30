package TVS_SFL.BearingHousingBackend.auth.services.impl;

import TVS_SFL.BearingHousingBackend.auth.entities.User;
import TVS_SFL.BearingHousingBackend.auth.repositories.UserRepository;
import TVS_SFL.BearingHousingBackend.auth.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
