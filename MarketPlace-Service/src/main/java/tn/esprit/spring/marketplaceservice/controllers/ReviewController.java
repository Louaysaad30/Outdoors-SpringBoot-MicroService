package tn.esprit.spring.marketplaceservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.marketplaceservice.DTO.ReviewRequestDTO;
import tn.esprit.spring.marketplaceservice.DTO.ReviewResponseDTO;
import tn.esprit.spring.marketplaceservice.entity.Produit;
import tn.esprit.spring.marketplaceservice.entity.Review;
import tn.esprit.spring.marketplaceservice.services.IMPL.ProduitServiceIMPL;
import tn.esprit.spring.marketplaceservice.services.IMPL.PythonService;
import tn.esprit.spring.marketplaceservice.services.interfaces.IReviewService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Gestion Review")
@RestController
@AllArgsConstructor
@RequestMapping("/Review")
@CrossOrigin(origins = "*")
public class ReviewController {

    // Injection des services nécessaires
    private final IReviewService reviewService;
    private final ProduitServiceIMPL produitService;
    @Autowired
    private PythonService pythonService;

    // Endpoint pour analyser toutes les revues
    @PostMapping("/analyze")
    public List<ReviewResponseDTO> analyzeReviews() {
        List<ReviewResponseDTO> responses = new ArrayList<>();

        // Récupérer toutes les revues depuis la base de données
        List<Review> allReviews = reviewService.getAllReviews();
        //tes
        // Traiter chaque revue
        for (Review review : allReviews) {
            ReviewResponseDTO response = new ReviewResponseDTO();
            response.setIdReview(Math.toIntExact(review.getIdReview()));
            response.setReviewText(review.getReviewText());
            response.setIdProduit(Math.toIntExact(review.getProduct().getIdProduit()));
            response.setDateCreation(review.getDateCreation());
            String sentiment = pythonService.analyzeSentiment(review.getReviewText());


            response.setSentiment(sentiment);



            // Récupérer la date de naissance (LocalDate) directement depuis la revue
            LocalDate birthdate = review.getDateDeNaissance();

            if (birthdate != null) {
                try {
                    // Calculer l'âge à partir de la date de naissance
                    int age = calculateAge(birthdate);
                    response.setAge(age);
                } catch (Exception e) {
                    response.setError("Erreur de calcul de l'âge pour la revue avec ID: " + review.getIdReview());
                }
            } else {
                response.setError("Date de naissance non fournie pour la revue avec ID: " + review.getIdReview());
            }

            responses.add(response);
        }

        return responses;
    }

    // Méthode pour convertir la date de naissance sous forme de liste [année, mois, jour]
    private LocalDate convertToDate(List<Integer> birthdateList) throws Exception {
        if (birthdateList.size() != 3) {
            throw new Exception("Invalid birthdate format");
        }

        int year = birthdateList.get(0);
        int month = birthdateList.get(1);
        int day = birthdateList.get(2);

        // Valider et retourner la date
        return LocalDate.of(year, month, day);
    }

    // Méthode pour calculer l'âge à partir de la date de naissance
    private int calculateAge(LocalDate birthdate) {
        LocalDate today = LocalDate.now();
        return today.getYear() - birthdate.getYear() - (today.getDayOfYear() < birthdate.getDayOfYear() ? 1 : 0);
    }

    // Endpoint pour ajouter une revue
    @PostMapping("/add")
    public Review addReview(@RequestBody Review review, @RequestParam(required = false) Long productId) {
        if (productId != null) {
            // Récupérer le produit par son ID
            Produit produit = produitService.getProduitsById(productId);
            if (produit != null) {
                review.setProduct(produit);
            }
        }
        return reviewService.addReview(review);
    }

    // Endpoint pour mettre à jour une revue
    @PutMapping("/update")
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    // Endpoint pour supprimer une revue
    @DeleteMapping("/delete/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    // Endpoint pour obtenir une revue par son ID
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    // Endpoint pour obtenir toutes les revues
    @GetMapping("/all")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // Endpoint pour obtenir les revues par ID du produit
    @GetMapping("/product/{productId}")
    public List<Review> getReviewsByProductId(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }
}
