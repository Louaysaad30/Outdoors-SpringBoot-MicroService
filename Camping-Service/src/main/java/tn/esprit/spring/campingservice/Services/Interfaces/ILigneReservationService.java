package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.Entity.LigneReservation;
import java.util.List;

public interface ILigneReservationService {
    List<LigneReservation> retrieveAllLigneReservations();
    LigneReservation addLigneReservation(LigneReservation ligneReservation);
    LigneReservation updateLigneReservation(LigneReservation ligneReservation);
    LigneReservation retrieveLigneReservation(Long idLigne);
    void removeLigneReservation(Long idLigne);
    void   removeLigneReservationsByReservationId(Long idReservation);
    LigneReservation updateLigneReservationByReservationId(Long idReservation, LigneReservation updatedLigneReservation);
}