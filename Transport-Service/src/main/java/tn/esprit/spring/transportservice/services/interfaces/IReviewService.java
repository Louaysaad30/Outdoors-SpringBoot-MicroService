package tn.esprit.spring.transportservice.services.interfaces;

import tn.esprit.spring.transportservice.entity.Review;

import java.util.List;

public interface IReviewService {

    List<Review> getAllReviews();

    List<Review> getReviewsByAgence(Long agenceId);

    Review getReviewById(Long id);

    Review getUserReview(Long vehiculeId, Long userId);

    Review updateReview(Long id, Review reviewDetails);
}
