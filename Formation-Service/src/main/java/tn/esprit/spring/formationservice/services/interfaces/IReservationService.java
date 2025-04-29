package tn.esprit.spring.formationservice.services.interfaces;

import tn.esprit.spring.formationservice.dto.ReservationRequest;
import tn.esprit.spring.formationservice.dto.ReservationResponse;
import tn.esprit.spring.formationservice.dto.UserReservationResponse;
import tn.esprit.spring.formationservice.entity.Reservation;

import java.util.List;

public interface IReservationService {

    Reservation addReservation(ReservationRequest request, Long userId);

    List<ReservationResponse> getAllReservations();

    List<ReservationResponse> getReservationsByParticipant(Long participantId);
    List<UserReservationResponse> getUserReservations(Long userId);
    void cancelReservation(Long id);


}
