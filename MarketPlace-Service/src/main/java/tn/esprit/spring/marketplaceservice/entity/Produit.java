// Produit.java
package tn.esprit.spring.marketplaceservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idProduit;
    String nomProduit;
    String descriptionProduit;
    String imageProduit;
    Double prixProduit;
    Long stockProduit;
    LocalDateTime dateCreation;
    @JsonIgnore
    @ManyToOne
    CodeProduit codeProduit;

    @ManyToOne
    PCategorie categorie;

    @JsonIgnore
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    List<LigneCommande> ligneCommandes;

    @Override
    public String toString() {
        return "Produit{id=" + idProduit + ", nom=" + nomProduit + ", description=" + descriptionProduit + ", prix=" + prixProduit + ", dateCreation=" + dateCreation + "}";
    }
}