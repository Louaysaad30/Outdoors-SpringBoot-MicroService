package tn.esprit.spring.formationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.formationservice.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByParticipantId(Long participantId);

    Optional<Reservation> findByParticipantIdAndFormationId(Long participantId, Long formationId);
}
