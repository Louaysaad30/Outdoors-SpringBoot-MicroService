package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.Entity.Reservation;
import tn.esprit.spring.campingservice.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface IReservationService {
    List<Reservation> retrieveAllReservations();
    Reservation addReservation(Reservation reservation);
    Reservation updateReservation(Reservation reservation);
    Reservation retrieveReservation(Long idReservation);
    List<Reservation> findReservationsByCentreId(Long centreId);
    Map<String, Object> checkAvailability(Map<String, Object> request);
    List<Reservation> findReservationsByClientId(Long idClient);
    void removeReservation(Long idReservation);
    List<Reservation> findConfirmedReservationsByCentreId(Long centreId);
    Reservation confirmReservation(Long idReservation);
    List<Reservation> findByClientIdAndPaidIsTrue(Long idClient);
}
