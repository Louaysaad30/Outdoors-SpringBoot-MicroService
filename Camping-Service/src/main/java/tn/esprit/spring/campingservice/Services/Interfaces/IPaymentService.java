package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.dto.ConfirmPaymentRequest;
import tn.esprit.spring.campingservice.dto.CreatePaymentRequest;
import tn.esprit.spring.campingservice.dto.CreatePaymentResponse;

import java.util.List;
import java.util.Map;

public interface IPaymentService {
    /*CreatePaymentResponse createPaymentIntent(CreatePaymentRequest request);
    void confirmPayment(ConfirmPaymentRequest request);*/
    List<Map<String, Object>> getPaymentHistory(Long clientId);

    CreatePaymentResponse processPayment(CreatePaymentRequest request);


}
