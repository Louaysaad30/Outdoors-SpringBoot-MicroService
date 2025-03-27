package tn.esprit.spring.marketplaceservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class LigneCommande {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long idLigneCommande;
        Long quantite;
        Double prix;

        @JsonIgnore
        @ManyToOne
        Produit produit;

        @JsonIgnore
        @ManyToOne
        Commande commande;

        @JsonIgnore
        @ManyToOne
        Panier panier;


}
