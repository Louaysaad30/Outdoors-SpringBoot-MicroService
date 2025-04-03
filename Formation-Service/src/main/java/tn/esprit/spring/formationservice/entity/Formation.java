package tn.esprit.spring.formationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 1500)
    private String description;

    private String imageUrl;

    private Double prix;

    private boolean publie;

    private LocalDateTime datePublication;

    private Long formateurId;

    private Long evenementId; // (optionnel, venant de Event-Service)

    @ManyToOne
    @JsonIgnoreProperties("formations")
    private Categorie categorie;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("formation")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("formation")
    private List<Pause> pauses;
}
