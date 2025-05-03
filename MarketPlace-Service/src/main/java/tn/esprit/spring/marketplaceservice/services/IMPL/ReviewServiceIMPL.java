package tn.esprit.spring.marketplaceservice.services.IMPL;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.marketplaceservice.entity.Review;
import tn.esprit.spring.marketplaceservice.repository.ReviewRepository;
import tn.esprit.spring.marketplaceservice.services.interfaces.IReviewService;

import java.util.List;

@AllArgsConstructor
@Service
public class ReviewServiceIMPL implements IReviewService {
    private final ReviewRepository reviewRepository;


    @Override
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdProduit(productId);
    }
}
