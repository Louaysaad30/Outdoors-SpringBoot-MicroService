package tn.esprit.spring.formationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.formationservice.entity.Review;
import tn.esprit.spring.formationservice.services.interfaces.IReviewService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        Review savedReview = reviewService.addReview(review);
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<Review>> getReviewsByFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(reviewService.getReviewsByFormation(formationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestParam("userId") Long userId // 🔐 vérifie que le bon user demande la suppression
    ) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/generate-ai")
    public ResponseEntity<Review> generateAIReview(@RequestBody Map<String, Object> body) {
        Long formationId = Long.valueOf(body.get("formationId").toString());
        String titreFormation = body.get("titre").toString(); // titre de la formation
        Double ratingDouble = Double.valueOf(body.get("rating").toString());
        int rating = ratingDouble.intValue(); // conversion explicite

        // Option 1 : userId est envoyé par le frontend
        Long userId = null;
        if (body.containsKey("userId")) {
            userId = Long.valueOf(body.get("userId").toString());
        }

        // Option 2 : sinon, on laisse la couche service détecter le user connecté via Feign
        Review aiReview = (userId != null)
                ? reviewService.generateAIReview(formationId, titreFormation, rating, userId)
                : reviewService.generateAIReview(formationId, titreFormation, rating); // surcharge

        return ResponseEntity.ok(aiReview);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review updated) {
        Review result = reviewService.updateReview(id, updated);
        return ResponseEntity.ok(result);
    }

}
