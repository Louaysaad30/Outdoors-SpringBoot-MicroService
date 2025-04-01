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
    private final VehiculeRepository vehiculeRepository;

    @Autowired
    public DemandeLocationService(DemandeLocationRepository demandeLocationRepository, VehiculeRepository vehiculeRepository) {
        this.demandeLocationRepository = demandeLocationRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public List<DemandeLocation> findAll() {
        return demandeLocationRepository.findAll();
    }

    @Override
    public DemandeLocation findById(Long id) {
        return demandeLocationRepository.findById(id).orElse(null);
    }

    @Override
    public DemandeLocation save(DemandeLocation demandeLocation) {

        if (demandeLocation.getVehicule() == null) {
            throw new RuntimeException("Véhicule ne peut pas être nul");
        }
        Vehicule vehicule = vehiculeRepository.findById(demandeLocation.getVehicule().getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        // Ensure the end date is after the start date
        long nbJours = ChronoUnit.DAYS.between(demandeLocation.getDebutLocation(), demandeLocation.getFinLocation());
        double prixTotal = nbJours * vehicule.getPrixParJour();
        demandeLocation.setPrixTotal(prixTotal);

        demandeLocation.setVehicule(vehicule);
        return demandeLocationRepository.save(demandeLocation);
    }


    @Override
    public DemandeLocation update(Long id, DemandeLocation demandeLocationDetails) {
        DemandeLocation existingDemande = demandeLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        existingDemande.setVehicule(demandeLocationDetails.getVehicule());
        existingDemande.setDebutLocation(demandeLocationDetails.getDebutLocation());
        existingDemande.setFinLocation(demandeLocationDetails.getFinLocation());
        existingDemande.setStatut(demandeLocationDetails.getStatut());
        long nbJours = ChronoUnit.DAYS.between(existingDemande.getDebutLocation(), existingDemande.getFinLocation());
        if (nbJours <= 0) {
            throw new RuntimeException("La date de fin doit être après la date de début");
        }
        double prixTotal = nbJours * existingDemande.getVehicule().getPrixParJour();
        existingDemande.setPrixTotal(prixTotal);
        return demandeLocationRepository.save(existingDemande);
    }

    @Override
    public void deleteById(Long id) {
        demandeLocationRepository.deleteById(id);
    }
}
