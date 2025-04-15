package tn.esprit.spring.marketplaceservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class Commande {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long idCommande;
        LocalDate dateCommande;
        Double montantCommande;
        String nom;
        Long phone;
        String email;
        String adresse;
        String gouvernement;
        String city;
        String shippingMethod;
        Long AdditionalService;
        Long userId;
        @Enumerated(EnumType.STRING)
        Status etat;
        String OrderNumber;



        @JsonIgnore
        @OneToMany(mappedBy = "commande")
        private List<LigneCommande> ligneCommande;

        @JsonIgnore
        @ManyToOne
        Livraison livraison;
}
