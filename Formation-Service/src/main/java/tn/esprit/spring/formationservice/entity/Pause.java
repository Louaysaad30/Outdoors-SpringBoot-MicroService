package tn.esprit.spring.formationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pause {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 1000)
    private String description;

    private String typePause; // (ex: animation, détente, repas...)

    @ManyToOne
    @JsonIgnoreProperties("pauses")
    private Sponsor sponsor;

    @ManyToOne
    @JsonIgnoreProperties("pauses")
    private Formation formation;
}
