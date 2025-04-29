package tn.esprit.spring.campingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.campingservice.Entity.LigneReservation;

import java.util.Date;
import java.util.List;

@Repository
public interface LigneReservationRepository extends JpaRepository<LigneReservation, Long> {

    List<LigneReservation> findByLogementIdLogementAndDateFinGreaterThanEqualAndDateDebutLessThanEqual(
            Long logementId, Date startDate, Date endDate);

    List<LigneReservation> findByMaterielIdMaterielAndDateFinGreaterThanEqualAndDateDebutLessThanEqual(
            Long materielId, Date startDate, Date endDate);

    List<LigneReservation> findByReservationIdReservation(Long reservationId);
}
