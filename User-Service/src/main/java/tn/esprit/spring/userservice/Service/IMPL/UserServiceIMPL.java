package tn.esprit.spring.userservice.Service.IMPL;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.UserService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceIMPL implements UserService {
    UserRepository userRepository;
    @Override
    public Optional<User> findByEmail(String email) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}
