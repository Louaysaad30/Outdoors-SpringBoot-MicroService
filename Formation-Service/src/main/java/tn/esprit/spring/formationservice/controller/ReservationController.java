package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.formationservice.dto.ReservationRequest;
import tn.esprit.spring.formationservice.dto.ReservationResponse; // 🆕
import tn.esprit.spring.formationservice.dto.UserReservationResponse;
import tn.esprit.spring.formationservice.entity.Reservation;
import tn.esprit.spring.formationservice.services.interfaces.IReservationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> addReservation(
            @RequestHeader("User-ID") Long userId,
            @RequestBody ReservationRequest request) {

        Reservation created = reservationService.addReservation(request, userId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAll() {  // 🆕 changer ici List<ReservationResponse>
        List<ReservationResponse> responses = reservationService.getAllReservations();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    @GetMapping("/participant/{id}")
    public ResponseEntity<List<ReservationResponse>> getByParticipant(@PathVariable Long id) {
        List<ReservationResponse> responses = reservationService.getReservationsByParticipant(id);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserReservationResponse>> getUserReservationsByUser(@PathVariable Long userId) {
        List<UserReservationResponse> reservations = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok("Reservation canceled successfully.");
    }

        @GetMapping("/stats")
        public ResponseEntity<Map<String, Object>> getStats() {
            List<ReservationResponse> reservations = reservationService.getAllReservations();

            // Comptage par statut (CONFIRME, EN_ATTENTE, ANNULE)
            Map<String, Long> statusStats = reservations.stream()
                    .collect(Collectors.groupingBy(ReservationResponse::getStatut, Collectors.counting()));

            // Comptage par jour (format : yyyy-MM-dd)
            Map<String, Long> dailyStats = reservations.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getDateReservation().toLocalDate().toString(),
                            Collectors.counting()
                    ));

            Map<String, Object> response = new HashMap<>();
            response.put("statusStats", statusStats); // PieChart
            response.put("dailyStats", dailyStats);   // BarChart

            return ResponseEntity.ok(response);
        }


}
