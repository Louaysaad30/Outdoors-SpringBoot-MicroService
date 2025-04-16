package tn.esprit.spring.userservice.Service.IMPL;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;
import tn.esprit.spring.userservice.Entity.User;
import tn.esprit.spring.userservice.Repository.ChatMessageRepository;
import tn.esprit.spring.userservice.Repository.ChatRoomRepository;
import tn.esprit.spring.userservice.Repository.UserRepository;
import tn.esprit.spring.userservice.Service.Interface.ChatMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceIMPL implements ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        try {
            Long senderId = Optional.ofNullable(chatMessage.getSender())
                    .map(User::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Sender cannot be null"));

            Long recipientId = Optional.ofNullable(chatMessage.getRecipient())
                    .map(User::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipient cannot be null"));

            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new IllegalArgumentException("Sender does not exist"));

            User recipient = userRepository.findById(recipientId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipient does not exist"));

            chatMessage.setSender(sender);
            chatMessage.setRecipient(recipient);

            // Search for an existing ChatRoom
            Optional<ChatRoom> chatRoomOptional = chatRoomRepository
                    .findBySenderIdAndRecipientIdOrSenderIdAndRecipientId(
                            chatMessage.getSender().getId(),
                            chatMessage.getRecipient().getId()
                    ).stream()
                    .findFirst();  // Get the first result (if it exists)

                    if (chatRoomOptional.isPresent()) {
                        // If a ChatRoom is found, set it on the message
                        chatMessage.setChatRoom(chatRoomOptional.get());
                    } else {
                        // If no ChatRoom is found, return an error or handle it as you see fit
                        throw new IllegalArgumentException("No existing ChatRoom found between these users.");
                    }

            return repository.save(chatMessage);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save chat message: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        try {
            // Try to fetch the chat room for sender -> recipient
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);

            // If no chat room found, check the opposite direction (recipient -> sender)
            if (!chatRoomOpt.isPresent()) {
                chatRoomOpt = chatRoomRepository.findBySenderIdAndRecipientId(recipientId, senderId);
            }

            // If a chat room is found, fetch the messages for that room
            return chatRoomOpt.map(room -> repository.findByChatRoom_Id(room.getId())) // Correct method name
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve chat messages: " + e.getMessage(), e);
        }
    }
}
