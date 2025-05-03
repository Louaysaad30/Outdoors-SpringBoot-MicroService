package tn.esprit.spring.formationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.formationservice.entity.Sponsor;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    boolean existsByNomOrContactEmail(String nom, String contactEmail);
    boolean existsByNomAndIdNot(String nom, Long id);
    boolean existsByContactEmailAndIdNot(String contactEmail, Long id);

}
