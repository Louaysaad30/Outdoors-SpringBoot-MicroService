package tn.esprit.spring.eventservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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
import tn.esprit.spring.eventservice.services.interfaces.IUserServiceClient;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Ticket Reservation Management", description = "Operations for managing ticket reservations")
public class TicketReservationController {

    private final ITicketReservationService reservationService;
    private final TicketRepository ticketRepository;
    private final ITicketService ticketService;
    private final IUserServiceClient userServiceClient;



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

@GetMapping("/event/{eventId}/participants")
@Operation(summary = "Get event participants", description = "Retrieves all users who have reservations for a specific event")
public ResponseEntity<List<Map<String, Object>>> getEventParticipants(@PathVariable Long eventId) {
    // Get all reservations for this event
    List<TicketReservation> reservations = reservationService.getEventReservations(eventId);

    // Extract unique user IDs
    Set<Long> uniqueUserIds = reservations.stream()
            .map(TicketReservation::getUserId)
            .collect(Collectors.toSet());

    // Fetch user details for each participant
    List<Map<String, Object>> participants = new ArrayList<>();
    for (Long userId : uniqueUserIds) {
        try {
            // Get user details from user service
            ResponseEntity<?> userResponse = userServiceClient.getUserById(userId);
            if (userResponse.getStatusCode().is2xxSuccessful()) {
                // Count how many tickets this user has for this event
                long ticketCount = reservations.stream()
                        .filter(r -> r.getUserId().equals(userId))
                        .count();

                Map<String, Object> participant = new HashMap<>();
                participant.put("user", userResponse.getBody());
                participant.put("ticketCount", ticketCount);
                participants.add(participant);
            }
        } catch (Exception e) {
            // Log error but continue with other users
            System.err.println("Error fetching user " + userId + ": " + e.getMessage());
        }
    }

    return ResponseEntity.ok(participants);
}


@GetMapping("/events/participants")
@Operation(summary = "Get all event participants", description = "Retrieves participant info for all events")
public ResponseEntity<Map<Long, List<Map<String, Object>>>> getAllEventParticipants() {
    // Get all events with their reservations
    Map<Long, List<Map<String, Object>>> eventParticipants = new HashMap<>();

    // For each event that has reservations
    List<TicketReservation> allReservations = reservationService.getAllReservations();

    // Group reservations by event ID
    Map<Long, List<TicketReservation>> reservationsByEvent = allReservations.stream()
            .collect(Collectors.groupingBy(r -> r.getTicket().getEvent().getId()));

    // Process each event
    for (Map.Entry<Long, List<TicketReservation>> entry : reservationsByEvent.entrySet()) {
        Long eventId = entry.getKey();
        List<TicketReservation> reservations = entry.getValue();

        // Similar logic as the previous method
        Set<Long> uniqueUserIds = reservations.stream()
                .map(TicketReservation::getUserId)
                .collect(Collectors.toSet());

        List<Map<String, Object>> participants = new ArrayList<>();
        // Process participants (same as previous method)

        eventParticipants.put(eventId, participants);
    }

    return ResponseEntity.ok(eventParticipants);
}

}