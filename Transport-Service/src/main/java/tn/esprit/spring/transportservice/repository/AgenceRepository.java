package tn.esprit.spring.transportservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.transportservice.entity.Agence;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
}