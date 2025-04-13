package tn.esprit.spring.userservice.Service.IMPL;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.ICloudinaryService;
import tn.esprit.spring.userservice.Service.Interface.UserService;
import tn.esprit.spring.userservice.dto.Request.UserUpdateRequest;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceIMPL implements UserService {
    UserRepository userRepository;
    private final ICloudinaryService cloudinaryService;


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



    public User updateUser(Long id, UserUpdateRequest request) throws IOException {
        User user = getUserById(id);

        // Handle image upload if a new file is provided
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(request.getImage());
                user.setImage(imageUrl); // Update with new Cloudinary URL
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error in uploading the image.");
            }

        }
        // Update other fields
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setTel(request.getTel());
        user.setDateNaissance(request.getDateNaissance());
        user.setEmail(request.getEmail());

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
