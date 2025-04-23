package tn.esprit.spring.userservice.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relate the ChatMessage to a specific ChatRoom
    @ManyToOne
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id")
    @JsonIgnore
    private ChatRoom chatRoom;

    // Relate the sender to the User entity
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @JsonIgnore
    private User sender;
    // Relate the recipient to the User entity
    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    @JsonIgnore
    private User recipient;
    private String content;
    private Date timestamp;
    @JsonProperty("isRead") // Explicitly specify the JSON property name
    private boolean isRead;

}
