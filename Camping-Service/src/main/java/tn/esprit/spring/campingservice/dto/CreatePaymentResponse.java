package tn.esprit.spring.campingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentResponse {
    private String clientSecret;
    private String paymentIntentId;
}