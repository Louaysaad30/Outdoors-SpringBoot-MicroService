package tn.esprit.spring.marketplaceservice.DTO;

import lombok.Data;
import tn.esprit.spring.marketplaceservice.entity.Status;

import java.time.LocalDateTime;

@Data
public class DeliveryStatusUpdateDto {
    private Long idLivraison;
    private Status etatLivraison;
    private LocalDateTime updateDate;
}
