package tn.esprit.spring.formationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FormationRequest {
    private String titre;
    private String description;
    private Double prix;
    private Long formateurId;
    private String mode;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Long categorieId;
    private String lieu;
    private String pauseTitle;
    private Integer pauseDuration;
    private boolean pauseSponsorRequired;
    private Long sponsorId;
}