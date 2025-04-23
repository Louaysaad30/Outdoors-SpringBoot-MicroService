// ITicketService.java
package tn.esprit.spring.eventservice.services.interfaces;

import tn.esprit.spring.eventservice.entity.Ticket;
import java.util.List;
import java.util.Optional;

public interface ITicketService {
    List<Ticket> getAllTickets();
    Optional<Ticket> getTicketById(Long id);
    Ticket saveTicket(Ticket ticket);
    Ticket updateTicket(Ticket ticket);
    void deleteTicket(Long id);
    List<Ticket> getTicketsByEventId(Long eventId);
// Add to ITicketService.java
Ticket applyDiscount(Long ticketId, String code, Double percentage);
Double calculateDiscountedPrice(Ticket ticket);

}