package tn.esprit.spring.transportservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.transportservice.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.vehicule.id = :vehiculeId")
    List<Review> findReviewsByVehiculeId(@Param("vehiculeId") Long vehiculeId);




}
