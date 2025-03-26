package tn.esprit.spring.userservice.Service.Interface;

import tn.esprit.spring.userservice.Entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
