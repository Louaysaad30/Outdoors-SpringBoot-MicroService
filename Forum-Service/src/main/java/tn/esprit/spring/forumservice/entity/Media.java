package tn.esprit.spring.forumservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column(nullable = false)
    private Integer userId ; // ID statique

    @Column(nullable = false)
    private String mediaUrl;  // URL du média dans le cloud (Google Cloud, S3, etc.)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;// Type du média (IMAGE ou VIDEO)


    @JsonIgnoreProperties("media") // This prevents the circular reference
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;  // Chaque média est associé à un seul post

}
