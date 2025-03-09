package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.Entity.Materiel;

import java.util.List;

public interface IMaterielService {
    List<Materiel> retrieveAllMateriels();
    Materiel addMateriel(Materiel materiel);
    Materiel updateMateriel(Materiel materiel);
    Materiel retrieveMateriel(Long idMateriel);
    void removeMateriel(Long idMateriel);
}
