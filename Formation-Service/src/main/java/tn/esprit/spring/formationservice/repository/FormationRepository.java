package tn.esprit.spring.formationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.formationservice.entity.Formation;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByFormateurId(Long formateurId);

}
