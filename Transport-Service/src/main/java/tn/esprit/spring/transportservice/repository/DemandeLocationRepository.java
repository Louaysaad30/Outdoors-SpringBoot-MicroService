package tn.esprit.spring.transportservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.transportservice.entity.DemandeLocation;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;


public interface DemandeLocationRepository extends JpaRepository<DemandeLocation, Long> {

    List<DemandeLocation> findByUserId(Long userId);

    List<DemandeLocation> findByVehiculeAgenceId(Long agenceId);


    @Query("SELECT d FROM DemandeLocation d WHERE d.vehicule.id = :vehicleId")
    List<DemandeLocation> findByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT d FROM DemandeLocation d WHERE d.vehicule.id = :vehicleId " +
            "AND d.statut = 'APPROUVÉE' " +
            "AND ((d.debutLocation BETWEEN :start AND :end) " +
            "OR (d.finLocation BETWEEN :start AND :end) " +
            "OR (d.debutLocation <= :start AND d.finLocation >= :end))")
    List<DemandeLocation> findConflictingReservations(
            @Param("vehicleId") Long vehicleId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT d FROM DemandeLocation d WHERE d.vehicule.id = :vehicleId " +
            "AND d.statut = 'APPROUVÉE' " +
            "AND d.finLocation > CURRENT_TIMESTAMP")
    List<DemandeLocation> findActiveReservations(@Param("vehicleId") Long vehicleId);


}
