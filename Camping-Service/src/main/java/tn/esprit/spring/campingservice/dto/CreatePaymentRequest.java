package tn.esprit.spring.campingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    private Long reservationId;
    private Long amount; // Amount in cents
    private String currency;
}
