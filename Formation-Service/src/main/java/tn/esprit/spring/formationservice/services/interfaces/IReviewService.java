package tn.esprit.spring.formationservice.services.interfaces;

import tn.esprit.spring.formationservice.entity.Review;
import java.util.List;

public interface IReviewService {
    Review addReview(Review review);
    List<Review> getReviewsByFormation(Long formationId);
    void deleteReview(Long reviewId, Long userId);
    Review generateAIReview(Long formationId, String titre, int rating);              // sans userId (auto)
    Review generateAIReview(Long formationId, String titre, int rating, Long userId); // avec userId fourni
    Review updateReview(Long id, Review updatedReview);
}
