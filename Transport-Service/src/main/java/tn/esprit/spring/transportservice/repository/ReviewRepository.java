package tn.esprit.spring.transportservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.transportservice.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVehiculeId(Long vehiculeId);
}
