package tn.esprit.spring.campingservice.Services.IMPL;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.campingservice.Entity.Review;
import tn.esprit.spring.campingservice.Repository.ReviewRepository;
import tn.esprit.spring.campingservice.Services.Interfaces.IReviewService;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceIMPL implements IReviewService {

    private ReviewRepository reviewRepository;

    @Override
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        if (!reviewRepository.existsById(review.getId())) {
            throw new EntityNotFoundException("Review not found with ID: " + review.getId());
        }
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(int id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    public Review getReviewById(int id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsByCenterId(int centerId) {
        return reviewRepository.findByCenterId(centerId);
    }

    @Override
    public List<Review> getReviewsByUserId(int userId) {
        return reviewRepository.findByUserId(userId);
    }
}