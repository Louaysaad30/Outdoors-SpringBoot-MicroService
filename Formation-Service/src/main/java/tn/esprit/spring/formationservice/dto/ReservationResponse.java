package tn.esprit.spring.formationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private LocalDateTime dateReservation;
    private String statut;

    private Long participantId;
    private String participantNom;
    private String participantPrenom;

    private Long formationId;
    private String formationTitre;
}
