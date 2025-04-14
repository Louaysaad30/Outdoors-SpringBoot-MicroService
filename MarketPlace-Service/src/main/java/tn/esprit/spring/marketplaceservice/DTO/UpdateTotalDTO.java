package tn.esprit.spring.marketplaceservice.DTO;

import lombok.Data;

@Data
public class UpdateTotalDTO {
    private Long idPanier;
    private Double total;
}
