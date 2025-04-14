package tn.esprit.spring.userservice.Service.Interface;

import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;

import java.util.List;

public interface ChatMessageService {

    public ChatMessage save(ChatMessage chatMessage);
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId);

    }
