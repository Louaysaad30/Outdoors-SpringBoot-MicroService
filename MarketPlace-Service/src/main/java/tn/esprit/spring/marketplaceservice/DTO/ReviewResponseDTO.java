package tn.esprit.spring.marketplaceservice.DTO;

import java.time.LocalDateTime;

public class ReviewResponseDTO {
    private Integer idReview;
    private String reviewText;
    private Integer age;
    private String error;
    private String sentiment; // <-- nouveau champ
    private Integer idProduit;
    private LocalDateTime dateCreation;





    // Getters et setters

    public Integer getIdReview() {
        return idReview;
    }

    public void setIdReview(Integer idReview) {
        this.idReview = idReview;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSentiment() {
        return sentiment;
    }
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    public Integer getIdProduit() {
        return idProduit;
    }
    public void setIdProduit(Integer idProduit) {
        this.idProduit = idProduit;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}

