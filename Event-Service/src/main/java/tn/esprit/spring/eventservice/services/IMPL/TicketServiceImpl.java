// TicketServiceImpl.java
package tn.esprit.spring.eventservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.eventservice.entity.Ticket;
import tn.esprit.spring.eventservice.repository.TicketRepository;
import tn.esprit.spring.eventservice.services.interfaces.ITicketService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {

    private final TicketRepository ticketRepository;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public List<Ticket> getTicketsByEventId(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    public Ticket applyDiscount(Long ticketId, String code, Double percentage) {
        return ticketRepository.findById(ticketId)
                .map(ticket -> {
                    // Store as "CODE:25" format
                    ticket.setDiscountCode(code + ":" + percentage);
                    return ticketRepository.save(ticket);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public Double calculateDiscountedPrice(Ticket ticket) {
        if (ticket.getDiscountCode() != null && ticket.getDiscountCode().contains(":")) {
            String[] parts = ticket.getDiscountCode().split(":");
            if (parts.length == 2) {
                try {
                    double percentage = Double.parseDouble(parts[1]);
                    return ticket.getPrice() * (1 - percentage / 100.0);
                } catch (NumberFormatException e) {
                    // Invalid format, return original price
                }
            }
        }
        return ticket.getPrice();
    }

}