package tn.esprit.spring.userservice.Service.IMPL;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Enum.EmailTemplateName;
import tn.esprit.spring.userservice.Enum.RoleType;
import tn.esprit.spring.userservice.Repository.TokenRepository;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.EmailService;
import tn.esprit.spring.userservice.Service.Interface.ICloudinaryService;
import tn.esprit.spring.userservice.Service.Interface.UserService;
import tn.esprit.spring.userservice.dto.Request.UserUpdateRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceIMPL implements UserService {
    UserRepository userRepository;
    TokenRepository  tokenRepository ;
    private final ICloudinaryService cloudinaryService;
    private final EmailService emailService; // <-- Add this



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
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }


        return userRepository.save(user);
    }
    @Override
    public User blockUser(Long id, boolean unblock) {
        User user = getUserById(id);
        user.setAccountLocked(!unblock);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        try {
            // Check if the user exists
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                // First, delete related records from the token table
                tokenRepository.deleteByUserId(id); // Assuming tokenRepository has a method to delete by user_id

                // Then, delete the user
                userRepository.deleteById(id);
            } else {
                // Log a message if the user was not found
                System.err.println("User with ID " + id + " not found.");
                throw new RuntimeException("User not found.");
            }
        } catch (Exception e) {
            // Log the error for debugging purposes
            System.err.println("Error occurred while deleting user with ID: " + id);
            e.printStackTrace();
            throw new RuntimeException("Failed to delete user with ID: " + id);
        }
    }


    @Override
    public User verifyUser(Long id) {
        User user = getUserById(id);
        user.setStatus(true);
        userRepository.save(user);

        try {
            String confirmationUrl = "http://localhost:4200/auth/signin"; // <-- Replace with actual URL
            String subject = "Account Verified 🎉";
            emailService.sendEmail(
                    user.getEmail(),
                    user.getNom(), // or use getPrenom() if you want first name
                    EmailTemplateName.CONFIRM_EMAIL, // or any template you have
                    confirmationUrl,
                    "Your account has been verified successfully.",
                    subject
            );
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        return user;
    }

    @Override
    public List<User> getUsersByRoleLivreur() {
        return userRepository.findByRolesRoleType(RoleType.LIVREUR);
    }


}


