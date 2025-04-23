package tn.esprit.spring.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.userservice.Entity.ChatMessage;
import tn.esprit.spring.userservice.Entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    List<ChatRoom> findBySenderIdOrRecipientId(Long senderId, Long recipientId);

    @Query("SELECT c FROM ChatRoom c WHERE (c.sender.id = :userId1 AND c.recipient.id = :userId2) OR (c.sender.id = :userId2 AND c.recipient.id = :userId1)")
    List<ChatRoom> findBySenderIdAndRecipientIdOrSenderIdAndRecipientId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.timestamp ASC")
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(@Param("chatRoomId") Long chatRoomId);

}
