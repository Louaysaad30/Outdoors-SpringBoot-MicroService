package tn.esprit.spring.transportservice.services.IMPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import tn.esprit.spring.transportservice.entity.Review;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.repository.ReviewRepository;
import tn.esprit.spring.transportservice.repository.VehiculeRepository;
import java.util.List;
import java.util.stream.DoubleStream;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    public Review addReview(Long vehiculeId, Review review) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new RuntimeException("Vehicule not found"));
        review.setVehicule(vehicule);
        review.setCreatedDate(LocalDateTime.now());

        return reviewRepository.save(review);
    }


    public List<Review> getReviews(Long vehiculeId) {
        return reviewRepository.findReviewsByVehiculeId(vehiculeId);
    }

    public void deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new RuntimeException("Review not found with ID: " + id);
        }
    }

}

