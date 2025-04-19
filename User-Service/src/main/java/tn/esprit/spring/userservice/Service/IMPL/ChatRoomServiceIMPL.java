package tn.esprit.spring.userservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.ChatRoom;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Repository.ChatRoomRepository;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.ChatRoomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceIMPL implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // Create or get existing ChatRoom ID
    @Override
    public Long createChatId(Long senderId, Long recipientId) {
        // Find sender and recipient by their IDs
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findById(recipientId).orElseThrow(() -> new RuntimeException("Recipient not found"));

        // Check if the chat room already exists
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);

        if (existingChatRoom.isPresent()) {
            // Return the existing room's ID
            return existingChatRoom.get().getId(); // ✅ use getId()
        }

        // Create a new chat room if it doesn't exist
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSender(sender);  // Set sender as User object
        chatRoom.setRecipient(recipient);  // Set recipient as User object

        // Save and return new room ID
        return chatRoomRepository.save(chatRoom).getId(); // ✅ use getId()
    }

    @Override
    public List<ChatRoom> getChatRoomsByUserId(Long userId) {
        // Get chat rooms where the user is either the sender or recipient
        List<ChatRoom> chatRooms = chatRoomRepository.findBySenderIdOrRecipientId(userId, userId);

        // Return an empty list if no chat rooms are found
        return chatRooms != null && !chatRooms.isEmpty() ? chatRooms : new ArrayList<>();
    }

    // Fetch ChatRoom by ID
    @Override
    public Optional<ChatRoom> getChatRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    @Override
    public List<ChatRoom> getChatRoomsBetweenUsers(Long userId1, Long userId2) {
        return chatRoomRepository.findBySenderIdAndRecipientIdOrSenderIdAndRecipientId(userId1, userId2);
    }

}
