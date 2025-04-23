package tn.esprit.spring.transportservice.services.interfaces;

import tn.esprit.spring.transportservice.entity.DemandeLocation;

import java.util.List;

public interface IDemandeLocationService {
    List<DemandeLocation> findAll();
    DemandeLocation findById(Long id);
    DemandeLocation save(DemandeLocation demandeLocation);

    DemandeLocation update(Long id, DemandeLocation demandeLocationDetails);

    void deleteById(Long id);
}
