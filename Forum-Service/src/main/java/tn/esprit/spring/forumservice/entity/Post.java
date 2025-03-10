package tn.esprit.spring.forumservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String content;  // Le texte du post, peut être vide si c'est uniquement un média

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean hasMedia = false; // Indique si le post contient un média

    @Column(nullable = false)
    private Integer userId = 10; // ID statique

    @Transient // Ne sera pas stocké en base
    private String username = "test_user";

    @Transient
    private String email = "test_user@example.com";

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Media> media;  // Liste des médias attachés au post (images, vidéos)

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;  // Liste des commentaires associés au post

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reaction> reactions;  // Liste des réactions sur le post
}