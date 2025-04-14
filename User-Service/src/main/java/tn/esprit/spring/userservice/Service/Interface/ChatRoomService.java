package tn.esprit.spring.userservice.Service.Interface;

import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.ChatRoom;

import java.util.List;
import java.util.Optional;

@Service
public interface ChatRoomService {
     Long createChatId(Long senderId, Long recipientId) ;
      List<ChatRoom> getChatRoomsByUserId(Long userId) ;
     Optional<ChatRoom> getChatRoomById(Long chatId) ;


    }
