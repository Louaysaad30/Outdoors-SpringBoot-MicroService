package tn.esprit.spring.userservice.Service.IMPL;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Entity.UserDetail;
import tn.esprit.spring.userservice.Enum.EmailTemplateName;
import tn.esprit.spring.userservice.Enum.Etat;
import tn.esprit.spring.userservice.Enum.RoleType;
import tn.esprit.spring.userservice.Repository.ChatMessageRepository;
import tn.esprit.spring.userservice.Repository.TokenRepository;
import tn.esprit.spring.userservice.Repository.UserDetailRepository;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.EmailService;
import tn.esprit.spring.userservice.Service.Interface.ICloudinaryService;
import tn.esprit.spring.userservice.Service.Interface.UserService;
import tn.esprit.spring.userservice.dto.Request.UserUpdateRequest;
import tn.esprit.spring.userservice.dto.Response.UserDetailDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.YearMonth;

@Service
@AllArgsConstructor
public class UserServiceIMPL implements UserService {

    UserDetailRepository userDetailRepository;
    UserRepository userRepository;
    TokenRepository  tokenRepository ;
    private final ICloudinaryService cloudinaryService;
    private final EmailService emailService;
    ChatMessageRepository chatMessageRepository;



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
    public void saveUser(User user) {
        user.setEtat(Etat.ONLINE);
        User savedUser = userRepository.save(user);
    }
    @Override
    public void incrementNavigation(Long userId) {
        UserDetail userDetail = userDetailRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserDetail not found"));

        userDetail.setTotalNavigationsFav1(userDetail.getTotalNavigationsFav1() + 1);
        userDetailRepository.save(userDetail);
    }
    @Override
    public UserDetailDTO getUserDetailDTOByUserId(Long userId) {
        UserDetail userDetail = userDetailRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserDetail not found"));

        return new UserDetailDTO(
                userDetail.getSessions(),
                userDetail.getTotalSessions(),
                userDetail.getNDaysAfterOnboarding(),
                userDetail.getTotalNavigationsFav1(),
                userDetail.getActivityDays()
        );
    }

    @Override
    public String predictChurn(Long userId) {
        UserDetailDTO dto = getUserDetailDTOByUserId(userId);
        try {
            // Convert the DTO to JSON for logging
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(dto);
            System.out.println("Request Data (JSON): " + json);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<UserDetailDTO> request = new HttpEntity<>(dto, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://127.0.0.1:5000/predict",  // Replace with your Flask endpoint
                    request,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error calling ML server: " + e.getMessage());
        }
    }
    @Override
    public Map<String, Long> getChurnStatistics() {
        List<User> users = userRepository.findAll(); // Fetch all users
        long churnCount = 0;
        long notChurnCount = 0;

        for (User user : users) {
            try {
                String result = predictChurn(user.getId()); // Call predictChurn for each user
                if (result.contains("\"prediction\": 1")) {
                    churnCount++;
                } else if (result.contains("\"prediction\": 0")) {
                    notChurnCount++;
                }
            } catch (Exception e) {
                System.err.println("Error predicting churn for user ID: " + user.getId() + " - " + e.getMessage());
            }
        }

        // Return the statistics as a map
        return Map.of(
                "churn", churnCount,
                "notChurn", notChurnCount
        );
    }
    public void sendEmailToChurnUsers() {
        List<User> users = userRepository.findAll(); // Fetch all users

        for (User user : users) {
            try {
                String result = predictChurn(user.getId()); // Call predictChurn for each user
                if (result.contains("\"prediction\": 1")) {
                    // Send email to the user
                    String subject = "We Miss You!";
                    String message = "We noticed you haven't been active lately. Come back and check out what's new!";
                    emailService.sendEmail(user.getEmail(), user.getNom(), EmailTemplateName.CHURN_EMAIL, null, message, subject);
                }
            } catch (Exception e) {
                System.err.println("Error predicting churn for user ID: " + user.getId() + " - " + e.getMessage());
            }
        }
    }
    public void incrementSessionStats(Long userId) {
        UserDetail userDetail = userDetailRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserDetail not found"));

        userDetail.setTotalSessions(userDetail.getTotalSessions() + 1);

        // Handle monthly sessions
        String currentMonth = YearMonth.now().toString();
        if (userDetail.getLastSessionMonth() == null || !userDetail.getLastSessionMonth().equals(currentMonth)) {
            userDetail.setSessions(1);
            userDetail.setLastSessionMonth(currentMonth);
        } else {
            userDetail.setSessions(userDetail.getSessions() + 1);
        }

        // Handle activityDays (distinct days)
        LocalDate today = LocalDate.now();
        if (userDetail.getLastActiveDate() == null || !userDetail.getLastActiveDate().equals(today)) {
            userDetail.setActivityDays(userDetail.getActivityDays() + 1);
            userDetail.setLastActiveDate(today);
        }

        // Handle nDaysAfterOnboarding
        long daysSinceOnboarding = java.time.Duration.between(userDetail.getCreatedAt(), LocalDateTime.now()).toDays();
        userDetail.setNDaysAfterOnboarding((int) daysSinceOnboarding);

        userDetailRepository.save(userDetail);
    }
    public void disconnect(User user) {
        var storedUser = userRepository.findById(user.getId()).orElse(null);
        if (storedUser != null) {
            storedUser.setEtat(Etat.OFFLINE);
            userRepository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByEtat(Etat.ONLINE);
    }
    public List<User> getUsersWithConversations(Long userId) {
        return chatMessageRepository.findUsersInConversationWith(userId);
    }

    @Override
    public List<User> getUsersByRoleLivreur() {
        return userRepository.findByRolesRoleType(RoleType.LIVREUR);
    }

}


