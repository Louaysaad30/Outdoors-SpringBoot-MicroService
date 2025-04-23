package tn.esprit.spring.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;
import tn.esprit.spring.userservice.Entity.User;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Correct the query method to use chatRoom.id
    List<ChatMessage> findByChatRoom_Id(Long chatRoomId); // Fetch messages by chatRoomId

    List<ChatRoom> findBySenderId(Long userId);
    @Query("SELECT DISTINCT u FROM User u WHERE u.id IN (" +
            "SELECT m.sender.id FROM ChatMessage m WHERE m.recipient.id = :userId " +
            "UNION " +
            "SELECT m.recipient.id FROM ChatMessage m WHERE m.sender.id = :userId)")
    List<User> findUsersInConversationWith(@Param("userId") Long userId);
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.id = :senderId AND m.recipient.id = :recipientId) OR " +
            "(m.sender.id = :recipientId AND m.recipient.id = :senderId) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findChatMessagesBetween(Long senderId, Long recipientId);

}

