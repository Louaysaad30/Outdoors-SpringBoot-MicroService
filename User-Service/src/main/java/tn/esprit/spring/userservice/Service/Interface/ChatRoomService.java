package tn.esprit.spring.userservice.Service.Interface;

import org.springframework.stereotype.Service;
import tn.esprit.spring.userservice.Entity.ChatRoom;

import java.util.List;
import java.util.Optional;

@Service
public interface ChatRoomService {
    Long createChatId(Long senderId, Long recipientId);
    List<ChatRoom> getChatRoomsByUserId(Long userId); // Garder cette méthode pour obtenir les chats d'un seul utilisateur
    Optional<ChatRoom> getChatRoomById(Long chatId);
    List<ChatRoom> getChatRoomsBetweenUsers(Long userId1, Long userId2); // Renommer pour éviter la duplication
}
