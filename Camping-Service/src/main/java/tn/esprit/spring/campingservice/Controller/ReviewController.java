package tn.esprit.spring.campingservice.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.campingservice.Entity.Review;
import tn.esprit.spring.campingservice.Services.Interfaces.IReviewService;

import java.util.List;

@Tag(name = "Review")
@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private IReviewService reviewService;

    @GetMapping("/all")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @PostMapping("/add")
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping("/update")
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping("/get/{id}")
    public Review getReview(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/center/{centerId}")
    public List<Review> getReviewsByCenterId(@PathVariable int centerId) {
        return reviewService.getReviewsByCenterId(centerId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUserId(@PathVariable int userId) {
        return reviewService.getReviewsByUserId(userId);
    }
}