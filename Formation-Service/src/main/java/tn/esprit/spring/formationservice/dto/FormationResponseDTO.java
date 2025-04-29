package tn.esprit.spring.formationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FormationResponseDTO {
    private Long id;
    private String titre;
    private String description;
    private Double prix;
    private String imageUrl;
    private boolean enLigne;
    private String lieu;
    private String meetLink;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private LocalDateTime datePublication;
    private Long formateurId;
    private String formateurNom;
    private String formateurPrenom;
    private String formateurImage;
    private String titrePause;
    private Integer dureePauseMinutes;
    private Boolean besoinSponsor;
    private Long categorieId;
    private Long sponsorId;
}
