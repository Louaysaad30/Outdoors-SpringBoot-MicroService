package tn.esprit.spring.formationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // ⭐ Entre 1 et 5
    private String title;
    @Column(length = 2000)
    private String comment;

    private String imageUrl; // facultatif, pour l'image attachée
    private LocalDateTime createdAt;

    private Long formationId; // 🔗 vers la Formation
    private Long userId; // 🔗 vers User pour afficher son nom/avatar
}
