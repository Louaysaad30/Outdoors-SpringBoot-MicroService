package tn.esprit.spring.marketplaceservice.services.interfaces;

import tn.esprit.spring.marketplaceservice.entity.Review;

import java.util.List;

public interface IReviewService {
    Review addReview(Review review);
    Review updateReview(Review review);
    void deleteReview(Long id);
    Review getReviewById(Long id);
    List<Review> getAllReviews();
    List<Review> getReviewsByProductId(Long productId);

}
