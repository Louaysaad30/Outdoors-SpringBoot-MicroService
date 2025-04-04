package tn.esprit.spring.transportservice.services.IMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.transportservice.entity.Review;
import tn.esprit.spring.transportservice.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getReviewsByVehicule(Long vehiculeId) {
        return reviewRepository.findByVehiculeId(vehiculeId);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Long id, Review updatedReview) {
        Optional<Review> existingReview = reviewRepository.findById(id);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(updatedReview.getRating());
            review.setComment(updatedReview.getComment());
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Review not found with ID: " + id);
        }
    }

    public void deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new RuntimeException("Review not found with ID: " + id);
        }
    }
}
