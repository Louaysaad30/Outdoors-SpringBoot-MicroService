package tn.esprit.spring.marketplaceservice.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idReview;

    String reviewText;
    int rating;
    String userName;
    Long userId;
    LocalDateTime dateCreation;
    LocalDate dateDeNaissance;
    String image;


    @JsonIgnore
    @ManyToOne
    Produit product;


}
