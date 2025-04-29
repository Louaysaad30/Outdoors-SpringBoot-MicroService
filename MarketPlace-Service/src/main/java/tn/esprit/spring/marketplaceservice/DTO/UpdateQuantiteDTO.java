package tn.esprit.spring.marketplaceservice.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateQuantiteDTO {
    private Long idLigneCommande;
    private int quantite;
    private Long idPanier;
    private double total;

}