package tn.esprit.spring.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.userservice.Entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    List<ChatRoom> findBySenderIdOrRecipientId(Long senderId, Long recipientId);

    @Query("SELECT c FROM ChatRoom c WHERE (c.sender.id = :userId AND c.recipient.id = :userId2) OR (c.sender.id = :userId2 AND c.recipient.id = :userId)")
    List<ChatRoom> findBySenderIdAndRecipientIdOrSenderIdAndRecipientId(Long userId, Long userId2);


}
