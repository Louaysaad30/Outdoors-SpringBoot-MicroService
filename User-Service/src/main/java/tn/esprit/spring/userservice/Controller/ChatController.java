package tn.esprit.spring.userservice.Controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Service.Interface.ChatMessageService;
import tn.esprit.spring.userservice.Service.Interface.ChatRoomService;
import tn.esprit.spring.userservice.Service.Interface.UserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ws")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    // Create a chat room between two users
    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(@RequestParam Long senderId,
                                            @RequestParam Long recipientId) {
        try {
            User sender = userService.getUserById(senderId);
            if (sender == null) {
                logger.error("Sender with ID {} not found.", senderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + senderId + " not found.");
            }

            User recipient = userService.getUserById(recipientId);
            if (recipient == null) {
                logger.error("Recipient with ID {} not found.", recipientId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + recipientId + " not found.");
            }

            Long chatId = chatRoomService.createChatId(senderId, recipientId);
            return ResponseEntity.ok(chatId);
        } catch (Exception e) {
            logger.error("Error creating chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating chat room: " + e.getMessage());
        }
    }

    // Check if a chat room exists between sender and recipient
    @GetMapping("/exists")
    public ResponseEntity<?> checkChatRoom(@RequestParam Long senderId,
                                           @RequestParam Long recipientId) {
        try {
            User sender = userService.getUserById(senderId);
            if (sender == null) {
                logger.error("Sender with ID {} not found.", senderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + senderId + " not found.");
            }

            User recipient = userService.getUserById(recipientId);
            if (recipient == null) {
                logger.error("Recipient with ID {} not found.", recipientId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + recipientId + " not found.");
            }

            // Fetch all chat rooms for the sender
            List<ChatRoom> rooms = chatRoomService.getChatRoomsByUserId(senderId);

            // Check if a chat room exists between sender and recipient
            Optional<ChatRoom> chatRoom = rooms.stream()
                    .filter(room -> room.getRecipient().equals(recipient))
                    .findFirst();

            if (chatRoom.isPresent()) {
                return ResponseEntity.ok(chatRoom.get().getId());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No chat room exists between the sender and recipient.");
            }

        } catch (Exception e) {
            logger.error("Error checking chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking chat room: " + e.getMessage());
        }
    }

    // Get all chat rooms for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getChatRoomsForUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                logger.error("User with ID {} not found.", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + userId + " not found.");
            }

            List<ChatRoom> chatRooms = chatRoomService.getChatRoomsByUserId(userId);

            if (chatRooms.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No chat rooms found for this user.");
            }

            return ResponseEntity.ok(chatRooms);
        } catch (Exception e) {
            logger.error("Error retrieving chat rooms: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving chat rooms: " + e.getMessage());
        }
    }

    // Get chat messages between sender and recipient
    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(@PathVariable Long senderId,
                                              @PathVariable Long recipientId) {
        try {
            User sender = userService.getUserById(senderId);
            if (sender == null) {
                logger.error("Sender with ID {} not found.", senderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + senderId + " not found.");
            }

            User recipient = userService.getUserById(recipientId);
            if (recipient == null) {
                logger.error("Recipient with ID {} not found.", recipientId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + recipientId + " not found.");
            }

            List<ChatMessage> messages = chatMessageService.findChatMessages(senderId, recipientId);

            if (messages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No messages found between the sender and recipient.");
            }

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            logger.error("Error retrieving messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving messages: " + e.getMessage());
        }
    }

    // WebSocket message handler
    @MessageMapping("/chat")
    public void processMessage(ChatMessage chatMessage) {
        try {
            chatMessage.setTimestamp(new Date());
            ChatMessage saved = chatMessageService.save(chatMessage);

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(chatMessage.getRecipient().getId()),
                    "/queue/messages",
                    saved
            );
        } catch (Exception e) {
            logger.error("Error processing chat message: {}", e.getMessage(), e);
        }
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getUsersWithConversations(@PathVariable Long userId) {
        try {
            User currentUser = userService.getUserById(userId);
            if (currentUser == null) {
                logger.error("User with ID {} not found.", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + userId + " not found.");
            }

            List<ChatRoom> chatRooms = chatRoomService.getChatRoomsByUserId(userId);

            List<User> users = chatRooms.stream()
                    .map(room -> {
                        if (room.getSender().getId().equals(userId)) {
                            return room.getRecipient();
                        } else {
                            return room.getSender();
                        }
                    })
                    .distinct()
                    .toList();

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error retrieving chat partners: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving chat partners: " + e.getMessage());
        }
    }

}