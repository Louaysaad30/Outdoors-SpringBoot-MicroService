package tn.esprit.spring.formationservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.spring.formationservice.dto.UserDto;
import tn.esprit.spring.formationservice.entity.Review;
import tn.esprit.spring.formationservice.repository.ReviewRepository;
import tn.esprit.spring.formationservice.services.interfaces.IHuggingFaceService;
import tn.esprit.spring.formationservice.services.interfaces.IReviewService;
import tn.esprit.spring.formationservice.services.interfaces.IUserServiceClient;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final IHuggingFaceService huggingFaceService;
    private final IUserServiceClient userServiceClient;

    @Override
    public Review addReview(Review review) {
        review.setId(null); // 🔥 Ajouter ceci pour forcer INSERT et éviter l'erreur Hibernate
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByFormation(Long formationId) {
        return reviewRepository.findByFormationId(formationId);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review introuvable"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres avis.");
        }

        reviewRepository.delete(review);
    }
    @Override
    public Review generateAIReview(Long formationId, String titre, int rating, Long userId) {
        String[] defaultTitles = {
                "Excellente formation",
                "Très instructif",
                "À recommander",
                "Bonne expérience",
                "Formation de qualité"
        };

        String[] defaultComments = {
                "Contenu bien structuré et facile à suivre.",
                "Le formateur explique très bien les concepts.",
                "J'ai beaucoup appris pendant cette formation.",
                "Très utile pour améliorer mes compétences.",
                "Support clair, exemples concrets. Bravo !"
        };

        String title = defaultTitles[(int)(Math.random() * defaultTitles.length)];
        String comment = defaultComments[(int)(Math.random() * defaultComments.length)];

        Review review = Review.builder()
                .formationId(formationId)
                .rating(rating)
                .title(title)
                .comment(comment)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }
    @Override
    public Review generateAIReview(Long formationId, String titre, int rating) {
        Long userId = getConnectedUserIdViaFeign(); // ou une valeur par défaut
        return generateAIReview(formationId, titre, rating, userId != null ? userId : 0L);
    }
    private Long getConnectedUserIdViaFeign() {
        try {
            List<UserDto> connectedUsers = userServiceClient.getConnectedUsers();
            if (connectedUsers != null && !connectedUsers.isEmpty()) {
                return connectedUsers.get(0).getId(); // premier utilisateur connecté
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'appel Feign à /user/connected", e);
        }
        return null;
    }
    @Override
    public Review updateReview(Long id, Review updatedReview) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review introuvable avec l'ID : " + id));

        // ⚠️ Vérification de sécurité (optionnelle selon ton besoin)
        if (!existing.getUserId().equals(updatedReview.getUserId())) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier cet avis.");
        }

        existing.setRating(updatedReview.getRating());
        existing.setTitle(updatedReview.getTitle());
        existing.setComment(updatedReview.getComment());
        existing.setImageUrl(updatedReview.getImageUrl());

        return reviewRepository.save(existing);
    }

}
