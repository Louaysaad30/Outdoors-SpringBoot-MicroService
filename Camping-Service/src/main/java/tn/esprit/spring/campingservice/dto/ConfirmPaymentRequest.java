package tn.esprit.spring.campingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmPaymentRequest {
    private String paymentIntentId;
    private Long reservationId;
}