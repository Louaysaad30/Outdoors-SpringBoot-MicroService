package tn.esprit.spring.userservice.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Entity.User;
import org.springframework.http.HttpStatus;
import tn.esprit.spring.userservice.Enum.EmailTemplateName;
import tn.esprit.spring.userservice.Service.Interface.EmailService;
import tn.esprit.spring.userservice.Service.Interface.UserService;
import tn.esprit.spring.userservice.dto.Request.UserUpdateRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private EmailService emailService;

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch users.");
        }
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        try {
            User user = userService.blockUser(id, false);
            return ResponseEntity.ok(Map.of("message", "User blocked successfully."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
        }
    }
    @PutMapping("/block-fail-by-email")
    public ResponseEntity<?> blockUserFailedByEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
            }

            userService.blockUser(user.getId(), false); // false = block
            emailService.sendEmail(user.getEmail(), user.fullName(), EmailTemplateName.FAIL, "", "", "");

            return ResponseEntity.ok(Map.of("message", "User blocked successfully."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while blocking the user: " + e.getMessage());
        }
    }



    @PutMapping("/unblock/{id}")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        try {
            User user = userService.blockUser(id, true);
            return ResponseEntity.ok(Map.of("message", "User unblocked successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
        }
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable Long id, @ModelAttribute @Valid UserUpdateRequest request) {
        try {
            User updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("status", ex.getStatusCode().value(), "message", ex.getReason()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
        }
    }



    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verifyUser(@PathVariable Long id) {
        try {
            User user = userService.verifyUser(id);
            return ResponseEntity.ok(Map.of("message", "User verified successfully.", "user", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id); // Call the service method to delete the user
            return ResponseEntity.ok(Map.of("message", "User deleted successfully."));

        } catch (RuntimeException e) {
            // Return error message if the user was not found or any other issue
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 🔹 GET connected users
    @GetMapping("/connected")
    public ResponseEntity<List<User>> getUserConnected() {
        List<User> connectedUsers = userService.findConnectedUsers();
        return ResponseEntity.ok(connectedUsers);
    }

    // 🔹 PUT connect a user (set ONLINE)
    @PutMapping("/connect/{id}")
    public ResponseEntity<String> connectUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        user.setEtat(tn.esprit.spring.userservice.Enum.Etat.ONLINE);
        userService.saveUser(user);
        return ResponseEntity.ok("User " + user.getNom() + " is now ONLINE");
    }
    // UserController.java
    @PostMapping("/increment-navigation")
    public ResponseEntity<String> incrementNavigation(@RequestParam Long userId) {
        userService.incrementNavigation(userId);
        return ResponseEntity.ok("Navigation incremented");
    }
    @PostMapping("/predict-churn")
    public ResponseEntity<String> predictChurn(@RequestParam Long userId) {
        String result = userService.predictChurn(userId);
        return ResponseEntity.ok(result);

    }
    @GetMapping("/churn-statistics")
    public ResponseEntity<Map<String, Long>> getChurnStatistics() {
        try {
            Map<String, Long> churnStatistics = userService.getChurnStatistics(); // Call the service method
            return ResponseEntity.ok(churnStatistics); // Return the statistics
        } catch (Exception e) {
            // Return a Map<String, Long> with default error values
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", -1L));
        }
    }
    @PostMapping("/send-churn-emails")
    public ResponseEntity<String> sendChurnEmails() {
        try {
            userService.sendEmailToChurnUsers(); // Call the service method
            return ResponseEntity.ok("Emails sent successfully to churn users.");
        } catch (Exception e) {
            // Handle any exceptions and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send emails to churn users: " + e.getMessage());
        }
    }
    @PutMapping("/disconnect/{id}")
    public ResponseEntity<String> disconnectUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        userService.disconnect(user);
        return ResponseEntity.ok("User " + user.getNom() + " is now OFFLINE");
    }

    @GetMapping("/role/livreur")
    public ResponseEntity<List<User>> getUsersByRoleLivreur() {
        List<User> livreurs = userService.getUsersByRoleLivreur();
        return ResponseEntity.ok(livreurs);
    }

}