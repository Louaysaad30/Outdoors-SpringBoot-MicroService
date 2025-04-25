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
            chatMessage.setRead(false); // Default to unread
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
    public List<ChatMessage> findChatMessagesBetween(Long senderId, Long recipientId) {
        return repository.findChatMessagesBetween(senderId, recipientId);
    }
    public void markMessageAsRead(Long messageId) {
        ChatMessage message = repository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        repository.save(message);
    }
    @Override
    public List<ChatMessage> findMessagesByChatRoomId(Long chatRoomId) {
        return chatRoomRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }

}
