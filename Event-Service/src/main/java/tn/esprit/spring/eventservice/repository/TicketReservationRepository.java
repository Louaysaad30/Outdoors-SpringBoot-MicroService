package tn.esprit.spring.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.eventservice.entity.TicketReservation;

import java.util.List;

@Repository
public interface TicketReservationRepository extends JpaRepository<TicketReservation, Long> {
    List<TicketReservation> findByUserId(Long userId);
    List<TicketReservation> findByTicketId(Long ticketId);
    List<TicketReservation> findByTicketEventId(Long eventId);
    boolean existsByUserIdAndTicketId(Long userId, Long ticketId);
    List<TicketReservation> findByUserIdAndTicketId(Long userId, Long ticketId);

}