package tn.esprit.spring.campingservice.Services.IMPL;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.campingservice.Entity.Reservation;
import tn.esprit.spring.campingservice.Services.Interfaces.IEmailService;
import tn.esprit.spring.campingservice.Services.Interfaces.IPaymentService;
import tn.esprit.spring.campingservice.Services.Interfaces.IReservationService;
import tn.esprit.spring.campingservice.Services.Interfaces.IUserService;
import tn.esprit.spring.campingservice.dto.ConfirmPaymentRequest;
import tn.esprit.spring.campingservice.dto.CreatePaymentRequest;
import tn.esprit.spring.campingservice.dto.CreatePaymentResponse;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceIMPL implements IPaymentService {

    private final IReservationService reservationService;
    private final IEmailService emailService;
    private final IUserService userService; // Add this dependency


    @Override
    public List<Map<String, Object>> getPaymentHistory(Long clientId) {
        // Get all reservations for the client that have been paid
        List<Reservation> paidReservations = reservationService.findByClientIdAndPaidIsTrue(clientId);
        List<Map<String, Object>> paymentHistory = new ArrayList<>();

        for (Reservation reservation : paidReservations) {
            Map<String, Object> payment = new HashMap<>();
            payment.put("reservationId", reservation.getIdReservation());
            payment.put("amount", reservation.getPrixTotal());
            payment.put("date", reservation.getDateDebut());
            payment.put("paymentId", reservation.getPaymentIntentId());
            payment.put("centerName", reservation.getCentre().getName());
            paymentHistory.add(payment);
        }

        return paymentHistory;
    }

    @Override
    @Transactional
    public CreatePaymentResponse processPayment(CreatePaymentRequest request) {
        try {
            // Validate the reservation exists
            Reservation reservation = reservationService.retrieveReservation(request.getReservationId());
            if (reservation == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reservation not found with ID: " + request.getReservationId());
            }

            // Ensure currency is lowercase
            String currency = request.getCurrency().toLowerCase();

            // Create Stripe Payment Intent with proper parameters for confirm=true
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount())
                    .setCurrency(currency)
                    .putMetadata("reservationId", String.valueOf(request.getReservationId()))
                    .setPaymentMethod("pm_card_visa") // Test card for Stripe
                    .setConfirm(true)
                    .setReturnUrl("https://yourwebsite.com/return") // Required for confirm=true
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Set reservation as confirmed and paid
            reservation.setConfirmed(true);
            reservation.setPaymentIntentId(paymentIntent.getId());
            reservationService.addReservation(reservation);

            // Send email confirmation
            // Send email confirmation
            try {
                Long clientId = reservation.getIdClient();
                UserDto user = userService.getUserById(clientId);

                if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                    emailService.sendPaymentConfirmationEmail(request.getReservationId(), user.getEmail());
                } else {
                    System.err.println("User email not found or empty for client ID: " + clientId);
                }
            } catch (Exception e) {
                // Log the error but don't fail the payment
                System.err.println("Error sending confirmation email: " + e.getMessage());
            }
            // Return client secret and payment intent ID
            return new CreatePaymentResponse(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId()
            );
        } catch (StripeException e) {
            // Log detailed error message
            System.err.println("Stripe Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error processing payment: " + e.getMessage());
        }
    }
}