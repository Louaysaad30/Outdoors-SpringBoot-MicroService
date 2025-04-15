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

        @ManyToOne
        Produit produit;


        @ManyToOne
        Commande commande;


        @ManyToOne
        Panier panier;

        @Override
        public String toString() {
                return "LigneCommande{id=" + idLigneCommande + ", produit=" + produit + ", quantite=" + quantite + ", prix=" + prix + "}";
        }


}
