package tn.esprit.spring.userservice.Service.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;
import tn.esprit.spring.userservice.Repository.ChatMessageRepository;
import tn.esprit.spring.userservice.Repository.ChatRoomRepository;
import tn.esprit.spring.userservice.Service.Interface.ChatMessageService;
import tn.esprit.spring.userservice.Service.Interface.ChatRoomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ChatMessageServiceIMPL implements ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        try {
            // Fetch the chat room by sender and recipient
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository
                    .findBySenderIdAndRecipientId(chatMessage.getSender().getId(), chatMessage.getRecipient().getId());

            if (!chatRoomOpt.isPresent()) {
                // If no chat room exists, create a new one
                ChatRoom newRoom = new ChatRoom();
                newRoom.setSender(chatMessage.getSender());
                newRoom.setRecipient(chatMessage.getRecipient());
                chatRoomOpt = Optional.of(chatRoomRepository.save(newRoom));
            }

            // Set the chat room in the chat message
            chatMessage.setChatRoom(chatRoomOpt.get());
            return repository.save(chatMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save chat message: " + e.getMessage(), e);
        }
    }
    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        try {
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
            return chatRoomOpt.map(room -> repository.findByChatRoom_Id(room.getId())) // Correct method name
                    .orElse(new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve chat messages: " + e.getMessage(), e);
        }
    }

  }
