package tn.esprit.spring.transportservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/{vehiculeId}")
    public ResponseEntity<Review> addReview(@PathVariable Long vehiculeId, @RequestBody Review review) {
        Review savedReview = reviewService.addReview(vehiculeId, review);
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/{vehiculeId}")
    public List<Review> getReviews(@PathVariable Long vehiculeId) {
        return reviewService.getReviews(vehiculeId);
    }

    @DeleteMapping("/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "Review with ID " + id + " has been deleted.";
    }
}
