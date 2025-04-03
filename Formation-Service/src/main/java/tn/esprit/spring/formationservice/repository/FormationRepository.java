package tn.esprit.spring.formationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.formationservice.entity.Formation;

public interface FormationRepository extends JpaRepository<Formation, Long> {
}
