package tn.esprit.spring.formationservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReservationResponse {
    private Long id; // id de la réservation
    private String statut; // EN_ATTENTE / CONFIRME / ANNULE
    private LocalDateTime dateReservation;
    private FormationSummary formation;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormationSummary {
        private String titre;
        private String imageUrl;
        private Double prix;
        private String duree;
        private LocalDateTime dateDebut;
    }
}
