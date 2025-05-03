package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.transportservice.entity.Review;
import tn.esprit.spring.transportservice.services.IMPL.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Add new review
    @PostMapping("/{vehiculeId}")
    public ResponseEntity<Review> addReview(@PathVariable Long vehiculeId, @RequestBody Review review) {
        Review savedReview = reviewService.addReview(vehiculeId, review);
        return ResponseEntity.ok(savedReview);
    }

    // Get all reviews for a vehicle
    @GetMapping("/{vehiculeId}")
    public List<Review> getReviews(@PathVariable Long vehiculeId) {
        return reviewService.getReviews(vehiculeId);
    }

    // Get single review by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    // Get user's review for a vehicle
    @GetMapping("/user/{vehiculeId}/{userId}")
    public ResponseEntity<Review> getUserReview(
            @PathVariable Long vehiculeId,
            @PathVariable Long userId) {
        Review review = reviewService.getUserReview(vehiculeId, userId);
        return ResponseEntity.ok(review);
    }

    // Update review
    @PatchMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long id,
            @RequestBody Review reviewDetails) {
        Review updatedReview = reviewService.updateReview(id, reviewDetails);
        return ResponseEntity.ok(updatedReview);
    }

    // Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review with ID " + id + " has been deleted.");
    }

    // Get reviews by agency
    @GetMapping("/agence/{agenceId}")
    public List<Review> getReviewsByAgence(@PathVariable Long agenceId) {
        return reviewService.getReviewsByAgence(agenceId);
    }

    // Get all reviews
    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews() {
        try {
            List<Review> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching reviews: " + e.getMessage());
        }
    }
}