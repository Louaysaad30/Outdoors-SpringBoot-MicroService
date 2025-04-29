package tn.esprit.spring.formationservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private Long formationId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String message;
}
