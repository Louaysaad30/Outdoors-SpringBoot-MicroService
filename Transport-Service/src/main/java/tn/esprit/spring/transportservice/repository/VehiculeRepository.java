package tn.esprit.spring.transportservice.repository;

import tn.esprit.spring.transportservice.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    List<Vehicule> findByDisponibleTrue();
    List<Vehicule> findByLocalisation(String localisation);
}
