package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.campingservice.Entity.Materiel;
import tn.esprit.spring.campingservice.Entity.Reservation;
import tn.esprit.spring.campingservice.Services.Interfaces.*;
import tn.esprit.spring.campingservice.dto.ConfirmPaymentRequest;
import tn.esprit.spring.campingservice.dto.CreatePaymentRequest;
import tn.esprit.spring.campingservice.dto.CreatePaymentResponse;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.List;
import java.util.Map;

@Tag(name = "Reservation")
@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/Reservation")
public class ReservationController {

    private IMaterielService materielService;

    private IReservationService reservationService;
    private IUserService userService;
    private IUserReservationService userReservationService;
    private IPaymentService paymentService;
    private IEmailService emailService;
    @GetMapping("/all")
    public List<Reservation> getAllReservations() {
        return reservationService.retrieveAllReservations();
    }

    @PostMapping("/add")
    public Reservation addReservation(@RequestBody Reservation reservation) {
        return reservationService.addReservation(reservation);
    }

    @PutMapping("/update")
    public Reservation updateReservation(@RequestBody Reservation reservation) {
        return reservationService.updateReservation(reservation);
    }

    @GetMapping("/get/{id}")
    public Reservation getReservation(@PathVariable Long id) {
        return reservationService.retrieveReservation(id);
    }

    /*@DeleteMapping("/delete/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.removeReservation(id);
    }*/

    @GetMapping("/byCentre/{centreId}")
    public List<Reservation> getReservationsByCentreId(@PathVariable Long centreId) {
        return reservationService.findReservationsByCentreId(centreId);
    }

    @PostMapping("/checkAvailability")
    public Map<String, Object> checkAvailability(@RequestBody Map<String, Object> request) {
        return reservationService.checkAvailability(request);
    }
    @GetMapping("/byClient/{idClient}")
    public List<Reservation> getReservationsByClientId(@PathVariable Long idClient) {
        return reservationService.findReservationsByClientId(idClient);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.removeReservation(id);
    }

    @GetMapping("/confirmed/byCentre/{centreId}")
    public List<Reservation> getConfirmedReservationsByCentreId(@PathVariable Long centreId) {
        return reservationService.findConfirmedReservationsByCentreId(centreId);
    }

    @PutMapping("/confirm/{idReservation}")
    public ResponseEntity<Reservation> confirmReservation(@PathVariable Long idReservation) {
        try {
            Reservation confirmedReservation = reservationService.confirmReservation(idReservation);
            return new ResponseEntity<>(confirmedReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    @GetMapping("/users/byCentre/{centreId}")
    public ResponseEntity<List<UserDto>> getUsersByCentreId(@PathVariable Long centreId) {
        List<UserDto> users = userReservationService.getUsersByCentreId(centreId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/history/{clientId}")
    public ResponseEntity<List<Map<String, Object>>> getPaymentHistory(@PathVariable Long clientId) {
        List<Map<String, Object>> paymentHistory = paymentService.getPaymentHistory(clientId);
        return ResponseEntity.ok(paymentHistory);
    }

    /*@PostMapping("/payment/create")
    public ResponseEntity<CreatePaymentResponse> createPaymentIntent(@RequestBody CreatePaymentRequest request) {
        CreatePaymentResponse response = paymentService.createPaymentIntent(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/confirm")
    public ResponseEntity<Void> confirmPayment(@RequestBody ConfirmPaymentRequest request) {
        paymentService.confirmPayment(request);
        return ResponseEntity.ok().build();
    }*/

    @PostMapping("/payment/process")
    public ResponseEntity<CreatePaymentResponse> processPayment(@RequestBody CreatePaymentRequest request) {
        CreatePaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/payment-confirmation/{reservationId}")
    public ResponseEntity<String> sendPaymentConfirmationEmail(
            @PathVariable Long reservationId,
            @RequestParam(required = false) String email) {
        try {
            emailService.sendPaymentConfirmationEmail(reservationId, email);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }
}
