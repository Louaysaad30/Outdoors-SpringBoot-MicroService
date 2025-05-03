package tn.esprit.spring.campingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.campingservice.Entity.Reservation;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationReository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCentreIdCentre(Long centreId);

    List<Reservation> findByCentreIdCentreAndDateFinGreaterThanEqualAndDateDebutLessThanEqual(
            Long centreId, Date startDate, Date endDate);

    List<Reservation> findByIdClient(Long idClient);
    @Query("SELECT r FROM Reservation r WHERE r.centre.idCentre = :centreId AND r.isConfirmed = true")
    List<Reservation> findConfirmedByCentreId(@Param("centreId") Long centreId);

    @Query("SELECT r FROM Reservation r WHERE r.idClient = :idClient AND r.isConfirmed = true")
    List<Reservation> findByIdClientAndIsConfirmedTrue(@Param("idClient") Long idClient);}
