package tn.esprit.spring.eventservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.eventservice.entity.TicketReservation;
import tn.esprit.spring.eventservice.services.interfaces.ITicketReservationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Ticket Reservation Management", description = "Operations for managing ticket reservations")
public class TicketReservationController {

    private final ITicketReservationService reservationService;

    @PostMapping
    @Operation(summary = "Reserve a ticket", description = "Creates a new ticket reservation for a user")
    public ResponseEntity<TicketReservation> reserveTicket(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long ticketId = request.get("ticketId");
        return new ResponseEntity<>(reservationService.reserveTicket(userId, ticketId), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user reservations", description = "Retrieves all reservations for a specific user")
    public ResponseEntity<List<TicketReservation>> getUserReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getUserReservations(userId));
    }

    @GetMapping("/ticket/{ticketId}")
    @Operation(summary = "Get ticket reservations", description = "Retrieves all reservations for a specific ticket")
    public ResponseEntity<List<TicketReservation>> getTicketReservations(@PathVariable Long ticketId) {
        return ResponseEntity.ok(reservationService.getTicketReservations(ticketId));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get event reservations", description = "Retrieves all reservations for a specific event")
    public ResponseEntity<List<TicketReservation>> getEventReservations(@PathVariable Long eventId) {
        return ResponseEntity.ok(reservationService.getEventReservations(eventId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel reservation", description = "Cancels a ticket reservation and returns the ticket to inventory")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}