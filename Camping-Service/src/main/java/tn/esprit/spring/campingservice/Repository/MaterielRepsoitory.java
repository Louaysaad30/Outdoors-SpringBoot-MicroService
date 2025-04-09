package tn.esprit.spring.campingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.campingservice.Entity.CentreCamping;
import tn.esprit.spring.campingservice.Entity.Materiel;

import java.util.List;

@Repository
public interface MaterielRepsoitory extends JpaRepository<Materiel, Long> {

    List<Materiel> findByCentre(CentreCamping centre);

}
