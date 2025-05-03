package tn.esprit.spring.marketplaceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.marketplaceservice.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Corrected method name to match the entity structure
    List<Review> findByProductIdProduit(Long idProduit);

    List<Review> findByUserId(Long userId);

    List<Review> findByRating(int rating);
}