package tn.esprit.spring.userservice.Service.Interface;

import tn.esprit.spring.userservice.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User updateUser(Long id, User request);
    User blockUser(Long id, boolean unblock);
    void deleteUser(Long id);}
