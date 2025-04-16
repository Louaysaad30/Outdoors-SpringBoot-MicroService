package tn.esprit.spring.eventservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.eventservice.entity.Ticket;
import tn.esprit.spring.eventservice.entity.TicketReservation;
import tn.esprit.spring.eventservice.repository.TicketRepository;
import tn.esprit.spring.eventservice.repository.TicketReservationRepository;
import tn.esprit.spring.eventservice.services.interfaces.ITicketReservationService;
import tn.esprit.spring.eventservice.services.interfaces.IUserServiceClient;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TicketReservationServiceImpl implements ITicketReservationService {

    private final TicketReservationRepository ticketReservationRepository;
    private final TicketRepository ticketRepository;
    private final IUserServiceClient userServiceClient;

    @Override
    @Transactional
    public TicketReservation reserveTicket(Long userId, Long ticketId) {
        // Verify user exists via User microservice
        if (!userServiceClient.userExists(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Get ticket and check availability
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        // Check if ticket is available
        if (ticket.getAvailableTickets() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tickets available");
        }

        // Check purchase limit
        int userTicketCount = ticketReservationRepository.findByUserIdAndTicketId(userId, ticketId).size();
        if (ticket.getPurchaseLimit() != null && userTicketCount >= ticket.getPurchaseLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Purchase limit reached for this ticket type");
        }

        // Create reservation
        TicketReservation reservation = TicketReservation.builder()
                .userId(userId)
                .ticket(ticket)
                .build();


// Generate compact reservation code
        String ticketTypePrefix = ticket.getType().name().substring(0, 1).toUpperCase();

        String compactEntityPart = "R" + userId + ticketTypePrefix + ticket.getEvent().getId() + "T" + ticket.getId();

        String dateTimePart = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyMMddHH"));

        String randomPart = String.format("%04X", new Random().nextInt(0xFFFF));

        String reservationCode = String.join("-", compactEntityPart, dateTimePart, randomPart);
        reservation.setReservationCode(reservationCode);

        // Decrement available tickets
        ticket.setAvailableTickets(ticket.getAvailableTickets() - 1);
        ticketRepository.save(ticket);

        return ticketReservationRepository.save(reservation);
    }

    @Override
    public List<TicketReservation> getUserReservations(Long userId) {
        return ticketReservationRepository.findByUserId(userId);
    }

    @Override
    public List<TicketReservation> getTicketReservations(Long ticketId) {
        return ticketReservationRepository.findByTicketId(ticketId);
    }

    @Override
    public List<TicketReservation> getEventReservations(Long eventId) {
        return ticketReservationRepository.findByTicketEventId(eventId);
    }

    @Override
    public Optional<TicketReservation> getReservationById(Long id) {
        return ticketReservationRepository.findById(id);
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        TicketReservation reservation = ticketReservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        // Increment available tickets
        Ticket ticket = reservation.getTicket();
        ticket.setAvailableTickets(ticket.getAvailableTickets() + 1);
        ticketRepository.save(ticket);

        ticketReservationRepository.deleteById(reservationId);
    }

    @Override
    public boolean hasUserReservedTicket(Long userId, Long ticketId) {
        return ticketReservationRepository.existsByUserIdAndTicketId(userId, ticketId);
    }
}