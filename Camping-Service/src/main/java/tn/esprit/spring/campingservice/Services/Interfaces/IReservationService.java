package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.Entity.Reservation;

import java.util.List;

public interface IReservationService {
    List<Reservation> retrieveAllReservations();
    Reservation addReservation(Reservation reservation);
    Reservation updateReservation(Reservation reservation);
    Reservation retrieveReservation(Long idReservation);

}
