package tn.esprit.spring.userservice.Service.IMPL;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.UserService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceIMPL implements UserService {
    UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }



    @Override
    public User updateUser(Long id, User request) {
        User user = getUserById(id);
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setTel(request.getTel());
        user.setDateNaissance(request.getDateNaissance());
        user.setStatus(request.getStatus());
        user.setImage(request.getImage());
        return userRepository.save(user);
    }

    @Override
    public User blockUser(Long id, boolean unblock) {
        User user = getUserById(id);
        user.setAccountLocked(!unblock);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
