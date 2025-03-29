package tn.esprit.spring.transportservice.services.IMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.transportservice.entity.DemandeLocation;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.repository.DemandeLocationRepository;
import tn.esprit.spring.transportservice.repository.VehiculeRepository;
import tn.esprit.spring.transportservice.services.interfaces.IDemandeLocationService;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DemandeLocationService implements IDemandeLocationService {
    private final DemandeLocationRepository demandeLocationRepository;

    public DemandeLocationService(DemandeLocationRepository demandeLocationRepository) {
        this.demandeLocationRepository = demandeLocationRepository;
    }

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Override
    public List<DemandeLocation> findAll() {
        return demandeLocationRepository.findAll();
    }

    @Override
    public DemandeLocation findById(Long id) {
        return demandeLocationRepository.findById(id).orElse(null);
    }

    @Override
    public DemandeLocation save(DemandeLocation demande) {
        // Ensure Vehicule is properly loaded from the DB
        Vehicule vehicule = vehiculeRepository.findById(demande.getVehicule().getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        // Ensure the vehicule's fields are populated (check its data)
        if (vehicule.getType() == null || vehicule.getModele() == null) {
            throw new RuntimeException("Véhicule incomplet");
        }

        // Calcul du nombre de nuits
        long nbJours = ChronoUnit.DAYS.between(demande.getDebutLocation(), demande.getFinLocation());
        if (nbJours <= 0) {
            throw new RuntimeException("La date de fin doit être après la date de début");
        }

        //Calcul du prix total
        double prixTotal = nbJours * vehicule.getPrixParJour();
        demande.setPrixTotal(prixTotal);

        // Ensure that the vehicule is fully set in the demande
        demande.setVehicule(vehicule);

        return demandeLocationRepository.save(demande);
    }

    @Override
    public void deleteById(Long id) {
        demandeLocationRepository.deleteById(id);
    }
}
