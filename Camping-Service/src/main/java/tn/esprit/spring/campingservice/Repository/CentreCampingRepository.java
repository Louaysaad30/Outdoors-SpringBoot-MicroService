package tn.esprit.spring.campingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.campingservice.Entity.CentreCamping;

import java.util.List;

@Repository
public interface CentreCampingRepository extends JpaRepository<CentreCamping, Long> {

    List<CentreCamping> findByIdOwner(Long idOwner);


    @Query("SELECT c FROM CentreCamping c WHERE c.isVerified = true")
    List<CentreCamping> findVerifiedCentreCamping();
}
