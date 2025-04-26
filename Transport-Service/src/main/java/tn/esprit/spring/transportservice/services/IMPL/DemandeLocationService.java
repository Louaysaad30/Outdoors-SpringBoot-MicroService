package tn.esprit.spring.transportservice.services.IMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.transportservice.entity.DemandeLocation;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.repository.DemandeLocationRepository;
import tn.esprit.spring.transportservice.repository.VehiculeRepository;
import tn.esprit.spring.transportservice.services.interfaces.IDemandeLocationService;

import java.time.LocalDateTime;
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

    public List<DemandeLocation> getDemandesByUserId(Long userId) {
        return demandeLocationRepository.findByUserId(userId);
    }

    @Override
    public DemandeLocation findById(Long id) {
        return demandeLocationRepository.findById(id).orElse(null);
    }

    @Override
    public DemandeLocation save(DemandeLocation demandeLocation) {
        validateDemandeLocation(demandeLocation);

        if (!isVehicleAvailable(
                demandeLocation.getVehicule().getId(),
                demandeLocation.getDebutLocation(),
                demandeLocation.getFinLocation())) {
            throw new RuntimeException("Le véhicule n'est pas disponible pour les dates sélectionnées");
        }

        calculatePrixTotal(demandeLocation);

        if (demandeLocation.getStatut() == null) {
            demandeLocation.setStatut(DemandeLocation.StatutDemande.EN_ATTENTE);
        }

        return demandeLocationRepository.save(demandeLocation);
    }

    @Override
    public boolean isVehicleAvailable(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        validateDates(startDate, endDate);

        List<DemandeLocation> conflicts = demandeLocationRepository.findConflictingReservations(
                vehicleId,
                startDate,
                endDate);

        return conflicts.stream()
                .noneMatch(d -> d.getStatut() == DemandeLocation.StatutDemande.APPROUVÉE);
    }



    @Override
    public DemandeLocation update(Long id, DemandeLocation demandeLocationDetails) {
        DemandeLocation existingDemande = demandeLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        existingDemande.setFullName(demandeLocationDetails.getFullName());
        existingDemande.setPhone(demandeLocationDetails.getPhone());
        existingDemande.setVehicule(demandeLocationDetails.getVehicule());
        existingDemande.setDebutLocation(demandeLocationDetails.getDebutLocation());
        existingDemande.setFinLocation(demandeLocationDetails.getFinLocation());
        existingDemande.setPickupLocation(demandeLocationDetails.getPickupLocation());

        existingDemande.setPickupLatitude(demandeLocationDetails.getPickupLatitude());
        existingDemande.setPickupLongitude(demandeLocationDetails.getPickupLongitude());

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

    public DemandeLocation updateStatut(Long id, DemandeLocation.StatutDemande statut) {
        DemandeLocation demande = findById(id);
        demande.setStatut(statut);
        demande.setCauseRejet(null); // Reset cause if approved
        return demandeLocationRepository.save(demande);
    }

    public DemandeLocation rejectDemande(Long id, String cause) {
        DemandeLocation demande = findById(id);
        demande.setStatut(DemandeLocation.StatutDemande.REJETÉE);
        demande.setCauseRejet(cause);
        return demandeLocationRepository.save(demande);
    }

    public List<DemandeLocation> getDemandesByAgence(Long agenceId) {
        return demandeLocationRepository.findByVehiculeAgenceId(agenceId);
    }

    @Override
    public List<DemandeLocation> getDemandeLocationsByVehicle(Long vehicleId) {
        return demandeLocationRepository.findByVehicleId(vehicleId);
    }

    @Override
    public List<DemandeLocation> findActiveReservations(Long vehicleId) {
        return demandeLocationRepository.findActiveReservations(vehicleId);
    }


    private void validateDemandeLocation(DemandeLocation demandeLocation) {
        if (demandeLocation.getVehicule() == null || demandeLocation.getVehicule().getId() == null) {
            throw new IllegalArgumentException("Un véhicule doit être spécifié");
        }

        // Check if vehicle exists without custom exception
        Vehicule vehicule = vehiculeRepository.findById(demandeLocation.getVehicule().getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID : " + demandeLocation.getVehicule().getId()));

        validateDates(demandeLocation.getDebutLocation(), demandeLocation.getFinLocation());
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (startDate.isBefore(now)) {
            throw new IllegalArgumentException("La date de début doit être dans le futur");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
    }

    private void calculatePrixTotal(DemandeLocation demandeLocation) {
        long nbJours = ChronoUnit.DAYS.between(
                demandeLocation.getDebutLocation(),
                demandeLocation.getFinLocation());

        if (nbJours <= 0) {
            throw new IllegalArgumentException("La durée de location doit être d'au moins un jour");
        }

        double prixTotal = nbJours * demandeLocation.getVehicule().getPrixParJour();
        demandeLocation.setPrixTotal(prixTotal);
    }

}
