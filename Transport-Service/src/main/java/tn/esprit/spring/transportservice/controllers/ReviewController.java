package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/vehicule/{vehiculeId}")
    public List<Review> getReviewsByVehicule(@PathVariable Long vehiculeId) {
        return reviewService.getReviewsByVehicule(vehiculeId);
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping("/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        return reviewService.updateReview(id, updatedReview);
    }

    @DeleteMapping("/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "Review with ID " + id + " has been deleted.";
    }
}
