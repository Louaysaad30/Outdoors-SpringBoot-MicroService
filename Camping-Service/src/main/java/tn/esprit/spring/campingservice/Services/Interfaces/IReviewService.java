package tn.esprit.spring.campingservice.Services.Interfaces;

import tn.esprit.spring.campingservice.Entity.Review;

import java.util.List;

public interface IReviewService {
    Review addReview(Review review);
    Review updateReview(Review review);
    void deleteReview(int id);
    Review getReviewById(int id);
    List<Review> getAllReviews();
    List<Review> getReviewsByCenterId(int centerId);
    List<Review> getReviewsByUserId(int userId);
}