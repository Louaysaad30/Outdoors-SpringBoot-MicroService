package tn.esprit.spring.transportservice.repository;

import tn.esprit.spring.transportservice.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    Optional<Vehicule> findById(Long id);
    List<Vehicule> findByAgenceId(Long agenceId);



}
