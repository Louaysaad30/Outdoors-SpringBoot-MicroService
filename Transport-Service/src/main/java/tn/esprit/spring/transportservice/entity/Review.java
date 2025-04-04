package tn.esprit.spring.transportservice.entity;

import jakarta.persistence.*;
import lombok.Setter;

import java.util.Date;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private int rating;
    @Setter
    private String comment;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();




    @Setter
    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule; // The vehicle being reviewed



//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user; // The user who wrote the review

    public Review() {}

    public Review(int rating, String comment, Vehicule vehicule) {
        this.rating = rating;
        this.comment = comment;
        this.vehicule = vehicule;
        this.createdDate = new Date();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }


    public Date getCreatedDate() {
        return createdDate;
    }

}


