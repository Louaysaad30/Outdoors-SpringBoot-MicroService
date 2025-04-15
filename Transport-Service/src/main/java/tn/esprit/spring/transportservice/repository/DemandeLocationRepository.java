package tn.esprit.spring.transportservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.transportservice.entity.DemandeLocation;

import java.util.List;


public interface DemandeLocationRepository extends JpaRepository<DemandeLocation, Long> {

    List<DemandeLocation> findByUserId(Long userId);

    List<DemandeLocation> findByVehiculeAgenceId(Long agenceId);


}
