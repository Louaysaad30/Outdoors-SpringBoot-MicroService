package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.Entity.Logement;

import java.util.List;

public interface ILogementService {
    List<Logement> retrieveAllLogements();
    Logement addLogement(Logement logement);
    Logement updateLogement(Logement logement);
    Logement retrieveLogement(Long idLogement);
    void removeLogement(Long idLogement);
}
