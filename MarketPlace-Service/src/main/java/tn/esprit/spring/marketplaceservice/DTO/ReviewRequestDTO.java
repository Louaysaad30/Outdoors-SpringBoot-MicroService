package tn.esprit.spring.marketplaceservice.DTO;

import java.time.LocalDate;
import java.util.List;

public class ReviewRequestDTO {
    private List<String> reviewText;
    private List<List<Integer>> birthdates; // La date sous forme de liste [année, mois, jour]
    private List<Integer> idReview;


    // Getters et setters

    public List<String> getReviewText() {
        return reviewText;
    }

    public void setReviewText(List<String> reviewText) {
        this.reviewText = reviewText;
    }

    public List<List<Integer>> getBirthdates() {
        return birthdates;
    }

    public void setBirthdates(List<List<Integer>> birthdates) {
        this.birthdates = birthdates;
    }

    public List<Integer> getIdReview() {
        return idReview;
    }

    public void setIdReview(List<Integer> idReview) {
        this.idReview = idReview;
    }
}

