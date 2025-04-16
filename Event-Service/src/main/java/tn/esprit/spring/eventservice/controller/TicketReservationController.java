package tn.esprit.spring.eventservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.eventservice.entity.Ticket;
import tn.esprit.spring.eventservice.entity.TicketReservation;
import tn.esprit.spring.eventservice.repository.TicketRepository;
import tn.esprit.spring.eventservice.repository.TicketReservationRepository;
import tn.esprit.spring.eventservice.services.interfaces.ITicketReservationService;
import tn.esprit.spring.eventservice.services.interfaces.ITicketService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Ticket Reservation Management", description = "Operations for managing ticket reservations")
public class TicketReservationController {

    private final ITicketReservationService reservationService;
    private final TicketRepository ticketRepository;
    private final ITicketService ticketService;
@PostMapping
@Operation(summary = "Reserve a ticket", description = "Creates a new ticket reservation for a user with optional discount code")
public ResponseEntity<TicketReservation> reserveTicket(@RequestBody Map<String, Object> request) {
    Long userId = Long.valueOf(request.get("userId").toString());
    Long ticketId = Long.valueOf(request.get("ticketId").toString());
    String discountCode = request.get("discountCode") != null ? request.get("discountCode").toString() : null;

    return new ResponseEntity<>(reservationService.reserveTicket(userId, ticketId, discountCode), HttpStatus.CREATED);
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


    @Operation(summary = "Check reservation limit", description = "Checks if a user has reached the purchase limit for a ticket")
    @GetMapping("/check-limit")
    public ResponseEntity<Map<String, Object>> checkReservationLimit(
            @RequestParam Long userId,
            @RequestParam Long ticketId) {

        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("canReserve", false, "message", "Ticket not found"));
        }

        Ticket ticket = ticketOpt.get();
        int userTicketCount = reservationService.getUserReservations(userId).stream()
                .filter(r -> r.getTicket().getId().equals(ticketId))
                .toList()
                .size();

        boolean canReserve = ticket.getPurchaseLimit() == null || userTicketCount < ticket.getPurchaseLimit();

        return ResponseEntity.ok(Map.of(
                "canReserve", canReserve,
                "currentCount", userTicketCount,
                "limit", ticket.getPurchaseLimit() != null ? ticket.getPurchaseLimit() : "unlimited"
        ));
    }



}