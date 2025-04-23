package tn.esprit.spring.userservice.Controller;

import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Enum.Etat;
import tn.esprit.spring.userservice.Service.Interface.ChatMessageService;
import tn.esprit.spring.userservice.Service.Interface.ChatRoomService;
import tn.esprit.spring.userservice.Service.Interface.UserService;
import tn.esprit.spring.userservice.dto.Request.ChatMessageDTO;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/ws")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private static Set<Long> connectedUserIds = ConcurrentHashMap.newKeySet();

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
            User recipient = userService.getUserById(recipientId);

            if (sender == null || recipient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("One or both users not found.");
            }

            List<ChatRoom> chatRooms = chatRoomService.getChatRoomsBetweenUsers(senderId, recipientId);

            if (!chatRooms.isEmpty()) {
                // Return the first chat room ID found
                return ResponseEntity.ok(chatRooms.get(0).getId());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No chat room exists between the users.");
            }

        } catch (Exception e) {
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
            User recipient = userService.getUserById(recipientId);

            if (sender == null || recipient == null) {
                String missingUser = (sender == null) ? "Sender" : "Recipient";
                Long missingId = (sender == null) ? senderId : recipientId;

                logger.error("{} with ID {} not found.", missingUser, missingId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(missingUser + " with ID " + missingId + " not found.");
            }

            List<ChatMessage> messages = chatMessageService.findChatMessagesBetween(senderId, recipientId);

            // Convert to DTOs
            List<ChatMessageDTO> response = messages.stream()
                    .map(msg -> new ChatMessageDTO(
                            msg.getId(),
                            msg.getContent(),
                            msg.getSender().getId(),
                            msg.getRecipient().getId(),
                            msg.getTimestamp(),
                            msg.isRead()
                    ))
                    .toList();


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving messages between {} and {}: {}", senderId, recipientId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving messages: " + e.getMessage());
        }
    }

    // WebSocket message handler
    @MessageMapping("/chat")
    public void processMessage(ChatMessageDTO chatMessageDTO) {
        try {
            // Create and populate ChatMessage entity
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(chatMessageDTO.getContent());
            chatMessage.setSender(userService.getUserById(chatMessageDTO.getSender()));
            chatMessage.setRecipient(userService.getUserById(chatMessageDTO.getRecipient()));
            chatMessage.setTimestamp(new Date());
            chatMessage.setRead(false);
            // Save the message
            ChatMessage saved = chatMessageService.save(chatMessage);
            // Send message to sender and recipient (only once if it's the same user)
            Set<Long> userIds = new HashSet<>(Arrays.asList(
                    chatMessageDTO.getSender(), chatMessageDTO.getRecipient()
            ));
            userIds.forEach(userId ->
                    messagingTemplate.convertAndSendToUser(
                            String.valueOf(userId), "/queue/messages", saved
                    )
            );

            // Send message to all subscribers
            messagingTemplate.convertAndSend("/queue/messages", chatMessage);
            logger.info("Message sent to users: {}", userIds);
        } catch (Exception e) {
            logger.error("Error processing chat message: {}", e.getMessage(), e);
        }
    }
    @MessageMapping("/user.online")
    public void userConnected(Map<String, Object> payload, Principal principal) {
        Long userId = Long.parseLong(String.valueOf(payload.get("userId")));
        connectedUserIds.add(userId);

        User user = userService.getUserById(userId);
        user.setEtat(Etat.ONLINE);
        userService.saveUser(user);

        List<User> connectedUsers = userService.findConnectedUsers();
        messagingTemplate.convertAndSend("/topic/connected-users", connectedUsers);
    }

    @MessageMapping("/user.offline")
    public void userDisconnected(Map<String, Object> payload, Principal principal) {
        System.out.println("🔌 Received offline status request");

        Long userId = Long.parseLong(String.valueOf(payload.get("userId")));
        System.out.println("🧍 User going offline: ID = " + userId);

        connectedUserIds.remove(userId);
        System.out.println("✅ Removed from connected user list");

        User user = userService.getUserById(userId);
        userService.disconnect(user);

        List<User> connectedUsers = userService.findConnectedUsers();
        messagingTemplate.convertAndSend("/topic/connected-users", connectedUsers);
        System.out.println("📤 Sent updated connected users list to /topic/connected-users");
    }


    // In your WebSocket controller
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getFirstNativeHeader("userId");

        if (userId != null) {
            connectedUserIds.remove(Long.parseLong(userId));
            User user = userService.getUserById(Long.parseLong(userId));
            user.setEtat(Etat.OFFLINE);
            userService.saveUser(user);
            List<User> connectedUsers = userService.findConnectedUsers();
            messagingTemplate.convertAndSend("/topic/connected-users", connectedUsers);
        }
    }
    @PutMapping("/mark-as-read/{messageId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long messageId) {
        chatMessageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Message marked as read"));
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

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addChatMessage(@RequestBody ChatMessage chatMessage) {
        try {
            System.out.println("🚨 Appel à /add avec message: " + chatMessage.getContent());

            // Validate sender and recipient
            if (chatMessage.getSender() == null || chatMessage.getRecipient() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Sender or recipient cannot be null.");
            }

            // Call the service to save the chat message
            ChatMessage savedMessage = chatMessageService.save(chatMessage);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
        } catch (RuntimeException e) {
            logger.error("Error saving message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving message: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/messages/room/{chatRoomId}")
    public ResponseEntity<?> getMessagesByChatRoomId(@PathVariable Long chatRoomId) {
        try {
            List<ChatMessage> messages = chatMessageService.findMessagesByChatRoomId(chatRoomId);

            if (messages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No messages found for this chat room.");
            }

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            logger.error("Error retrieving messages for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving messages: " + e.getMessage());
        }
    }
    // Example of WebSocket handler for message read
    @MessageMapping("/readMessage")
    public void handleMessageRead(@Payload String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            Long messageId = jsonObject.getLong("messageId");
            Long senderId = jsonObject.getLong("senderId");

            // 1. Update in database
            chatMessageService.markMessageAsRead(messageId);

            // 2. Send read receipt to sender
            messagingTemplate.convertAndSend(

                    "/queue/read-receipts",
                    Map.of(
                            "type", "message-read",
                            "messageId", messageId
                    )
            );

            System.out.println("Sent read receipt for message: " + messageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}