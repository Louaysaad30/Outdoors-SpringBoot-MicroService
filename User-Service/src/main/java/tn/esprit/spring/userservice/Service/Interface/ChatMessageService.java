package tn.esprit.spring.userservice.Service.Interface;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;

import java.util.List;

public interface ChatMessageService {

    public ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findChatMessagesBetween(Long senderId, Long recipientId);
     void markMessageAsRead(Long messageId) ;

    List<ChatMessage> findMessagesByChatRoomId(Long chatRoomId);

}
