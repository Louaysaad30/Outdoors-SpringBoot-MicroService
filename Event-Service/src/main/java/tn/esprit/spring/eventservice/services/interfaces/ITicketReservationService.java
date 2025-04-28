package tn.esprit.spring.eventservice.services.interfaces;

import tn.esprit.spring.eventservice.entity.TicketReservation;
import java.util.List;
import java.util.Optional;

public interface ITicketReservationService {
    TicketReservation reserveTicket(Long userId, Long ticketId, String discountCode);
    List<TicketReservation> getUserReservations(Long userId);
    List<TicketReservation> getTicketReservations(Long ticketId);
    List<TicketReservation> getEventReservations(Long eventId);
    Optional<TicketReservation> getReservationById(Long id);
    void cancelReservation(Long reservationId);
    boolean hasUserReservedTicket(Long userId, Long ticketId);
    List<TicketReservation> getAllReservations();
}