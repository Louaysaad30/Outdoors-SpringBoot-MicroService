package tn.esprit.spring.userservice.Service.Interface;

import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.dto.Request.UserUpdateRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User updateUser(Long id, UserUpdateRequest request) throws IOException;
    User blockUser(Long id, boolean unblock);
    void deleteUser(Long id);
    User verifyUser(Long id);
    List<User> getUsersByRoleLivreur();

}
