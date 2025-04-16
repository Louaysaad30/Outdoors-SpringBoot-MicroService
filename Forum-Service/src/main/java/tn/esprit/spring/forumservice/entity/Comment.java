package tn.esprit.spring.forumservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String content;  // Contenu du commentaire

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer userId ; // ID statique

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;  // Le post auquel le commentaire appartient

    @JsonIgnoreProperties("parentComment")
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies;  // Liste des réponses à ce commentaire

    @JsonIgnoreProperties("replies")
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;  // Le commentaire auquel ce commentaire répond (peut être null)


}
