package tn.esprit.spring.campingservice.Services.Interfaces;

public interface IEmailService {
    void sendPaymentConfirmationEmail(Long reservationId, String email);
}