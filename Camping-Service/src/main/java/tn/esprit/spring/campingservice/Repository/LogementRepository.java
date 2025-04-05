package tn.esprit.spring.campingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Entity.Logement;

import java.util.List;

@Repository
public interface LogementRepository extends JpaRepository<Logement, Long> {
    List<Logement> findByCentre(CentreCamping centre);

}
