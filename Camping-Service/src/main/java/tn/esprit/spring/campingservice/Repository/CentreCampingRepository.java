package tn.esprit.spring.campingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.campingservice.Entity.CentreCamping;

@Repository
public interface CentreCampingRepository extends JpaRepository<CentreCamping, Long> {
}
