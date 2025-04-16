package tn.esprit.spring.marketplaceservice.DTO;

import lombok.Data;

@Data
public class UpdateQuantiteDTO {
    private Long idLigneCommande;
    private int quantite;
    private Long idPanier;
    private double total;
}