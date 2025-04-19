package tn.esprit.spring.transportservice.services.IMPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import tn.esprit.spring.transportservice.entity.Review;
import tn.esprit.spring.transportservice.entity.Vehicule;
import tn.esprit.spring.transportservice.repository.ReviewRepository;
import tn.esprit.spring.transportservice.repository.VehiculeRepository;
import tn.esprit.spring.transportservice.services.interfaces.IReviewService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

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


    @Override
    public List<Review> getReviewsByAgence(Long agenceId) {
        List<Vehicule> vehicules = vehiculeRepository.findByAgenceId(agenceId);  // Récupérer les véhicules de l'agence
        List<Review> allReviews = new ArrayList<>();

        for (Vehicule v : vehicules) {
            List<Review> reviewsForVehicule = reviewRepository.findReviewsByVehiculeId(v.getId());  // Récupérer les avis des véhicules
            allReviews.addAll(reviewsForVehicule);
        }
        return allReviews;
    }





}

