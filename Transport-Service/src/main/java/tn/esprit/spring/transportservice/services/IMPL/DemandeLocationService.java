package tn.esprit.spring.transportservice.services.IMPL;

import jakarta.transaction.Transactional;
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
        return demandeLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DemandeLocation non trouvée avec l'ID : " + id));
    }

    @Override
    @Transactional
    public DemandeLocation save(DemandeLocation demandeLocation) {
        validateDemandeLocation(demandeLocation);

        if (demandeLocation.getVehicule() == null || demandeLocation.getVehicule().getId() == null) {
            throw new IllegalArgumentException("Le véhicule ou son identifiant est manquant.");
        }

        boolean disponible = isVehicleAvailable(
                demandeLocation.getVehicule().getId(),
                demandeLocation.getDebutLocation(),
                demandeLocation.getFinLocation()
        );

        if (!disponible) {
            throw new IllegalStateException("Le véhicule n'est pas disponible pour les dates sélectionnées.");
        }

        Vehicule vehicule = vehiculeRepository.findById(demandeLocation.getVehicule().getId())
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID : " + demandeLocation.getVehicule().getId()));

        demandeLocation.setVehicule(vehicule);

        if (demandeLocation.getStatut() == null) {
            demandeLocation.setStatut(DemandeLocation.StatutDemande.EN_ATTENTE);
        }

        long nbJours = ChronoUnit.DAYS.between(demandeLocation.getDebutLocation(), demandeLocation.getFinLocation());
        double prixTotal = nbJours * vehicule.getPrixParJour();
        demandeLocation.setPrixTotal(prixTotal);

        demandeLocation.setVehicule(vehicule);


        return demandeLocationRepository.save(demandeLocation);
    }


    @Override
    public boolean isVehicleAvailable(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        validateDates(startDate, endDate);

        List<DemandeLocation> conflictingReservations = demandeLocationRepository.findConflictingReservations(
                vehicleId, startDate, endDate
        );
        return conflictingReservations.stream()
                .noneMatch(reservation -> reservation.getStatut() == DemandeLocation.StatutDemande.APPROUVÉE);
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
        // Calcul précis de la durée en jours (arrondi au jour supérieur)
        long heuresLocation = ChronoUnit.HOURS.between(
                demandeLocation.getDebutLocation(),
                demandeLocation.getFinLocation());

        // Toute durée < 24h compte comme 1 jour
        long nbJours = (heuresLocation + 23) / 24;

        // Calcul du prix total avec arrondi
        double prixTotal = Math.round(nbJours *
                demandeLocation.getVehicule().getPrixParJour() * 100) / 100.0;

        demandeLocation.setPrixTotal(prixTotal);
    }


}
