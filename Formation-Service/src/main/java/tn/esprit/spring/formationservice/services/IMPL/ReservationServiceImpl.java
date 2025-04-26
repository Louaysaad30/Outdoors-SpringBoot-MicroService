package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.formationservice.entity.Reservation;
import tn.esprit.spring.formationservice.repository.ReservationRepository;
import tn.esprit.spring.formationservice.services.interfaces.IReservationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;

    @Override
    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getReservationsByParticipant(Long participantId) {
        return reservationRepository.findByParticipantId(participantId);
    }
}
