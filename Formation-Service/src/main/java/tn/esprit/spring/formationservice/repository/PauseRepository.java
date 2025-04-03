package tn.esprit.spring.formationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.formationservice.entity.Pause;

import java.util.List;

@Repository
public interface PauseRepository extends JpaRepository<Pause, Long> {
    List<Pause> findByFormationId(Long formationId);
}
