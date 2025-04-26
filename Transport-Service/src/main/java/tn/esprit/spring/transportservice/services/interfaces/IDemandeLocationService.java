package tn.esprit.spring.transportservice.services.interfaces;

import tn.esprit.spring.transportservice.entity.DemandeLocation;

import java.time.LocalDateTime;
import java.util.List;

public interface IDemandeLocationService {
    List<DemandeLocation> findAll();
    List<DemandeLocation> getDemandesByUserId(Long userId);
    DemandeLocation findById(Long id);
    DemandeLocation save(DemandeLocation demandeLocation);
    DemandeLocation update(Long id, DemandeLocation demandeLocationDetails);
    void deleteById(Long id);
    DemandeLocation updateStatut(Long id, DemandeLocation.StatutDemande statut);
    DemandeLocation rejectDemande(Long id, String cause);
    List<DemandeLocation> getDemandesByAgence(Long agenceId);
    List<DemandeLocation> getDemandeLocationsByVehicle(Long vehicleId);
    boolean isVehicleAvailable(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate);
    List<DemandeLocation> findActiveReservations(Long vehicleId);
}
