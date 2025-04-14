package tn.esprit.spring.formationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long participantId; // venant de User-Service

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    private LocalDateTime dateReservation;

    @ManyToOne
    @JsonIgnoreProperties("reservations")
    private Formation formation;
}
