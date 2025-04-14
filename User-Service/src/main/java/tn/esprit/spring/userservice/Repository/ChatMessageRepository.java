package tn.esprit.spring.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Correct the query method to use chatRoom.id
    List<ChatMessage> findByChatRoom_Id(Long chatRoomId); // Fetch messages by chatRoomId

    List<ChatRoom> findBySenderId(Long userId);
}

