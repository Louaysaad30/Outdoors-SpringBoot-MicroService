package tn.esprit.spring.forumservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Table(name = "reactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private Integer userId = 10; // ID statique

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;  // Réaction donnée à un post



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;  // Type de réaction (par exemple : J'aime, J'adore, etc.)

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
