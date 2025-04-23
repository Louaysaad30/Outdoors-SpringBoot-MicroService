package tn.esprit.spring.userservice.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.userservice.Enum.RoleType;
import tn.esprit.spring.userservice.Service.Interface.StatisticsService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/total-users")
    public ResponseEntity<Object> getTotalUsers() {
        try {
            long totalUsers = statisticsService.getTotalUsers();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("totalUsers", totalUsers));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/verified-users")
    public ResponseEntity<Object> getVerifiedUsers() {
        try {
            long verifiedUsers = statisticsService.getVerifiedUsers();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("verifiedUsers", verifiedUsers));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/non-verified-users")
    public ResponseEntity<Object> getNonVerifiedUsers() {
        try {
            long nonVerifiedUsers = statisticsService.getNonVerifiedUsers();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("nonVerifiedUsers", nonVerifiedUsers));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/blocked-users")
    public ResponseEntity<Object> getBlockedUsers() {
        try {
            long blockedUsers = statisticsService.getBlockedUsers();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("blockedUsers", blockedUsers));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/non-blocked-users")
    public ResponseEntity<Object> getNonBlockedUsers() {
        try {
            long nonBlockedUsers = statisticsService.getNonBlockedUsers();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("nonBlockedUsers", nonBlockedUsers));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/users-by-role")
    public ResponseEntity<Object> getUsersByRole() {
        try {
            Map<RoleType, Long> usersByRole = statisticsService.getUsersByRole();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("usersByRole", usersByRole));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/total-messages")
    public ResponseEntity<Object> getTotalMessages() {
        try {
            long totalMessages = statisticsService.getTotalMessages();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("totalMessages", totalMessages));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/unread-messages")
    public ResponseEntity<Object> getUnreadMessages() {
        try {
            long unreadMessages = statisticsService.getUnreadMessages();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("unreadMessages", unreadMessages));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/messages-by-user")
    public ResponseEntity<Object> getMessagesByUser() {
        try {
            Map<Long, Long> messagesByUser = statisticsService.getMessagesByUser();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("messagesByUser", messagesByUser));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/messages-by-chat-room")
    public ResponseEntity<Object> getMessagesByChatRoom() {
        try {
            Map<Long, Long> messagesByChatRoom = statisticsService.getMessagesByChatRoom();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap("messagesByChatRoom", messagesByChatRoom));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    // Gestion des erreurs spécifiques
    private ResponseEntity<Object> handleException(Exception e) {
        if (e instanceof ResponseStatusException) {
            // Si l'exception est une ResponseStatusException
            ResponseStatusException ex = (ResponseStatusException) e;
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("status", ex.getStatusCode().value(), "message", ex.getReason()));
        }

        // Gérer les autres types d'erreurs
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "Une erreur est survenue"));
    }
}
