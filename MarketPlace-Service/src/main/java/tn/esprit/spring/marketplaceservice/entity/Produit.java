package tn.esprit.spring.marketplaceservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    String imageProduit; // Keep main image for backward compatibility
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

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> imageGallery = new ArrayList<>();



    // Helper method to add an image to gallery
    public void addImage(String imageUrl) {
        ProductImage image = new ProductImage();
        image.setImageUrl(imageUrl);
        image.setProduit(this);
        image.setDisplayOrder(this.imageGallery.size() + 1);
        this.imageGallery.add(image);
    }

    // Helper method to remove an image
    public void removeImage(Long imageId) {
        this.imageGallery.removeIf(image -> image.getIdImage().equals(imageId));
        // Reorder remaining images
        for (int i = 0; i < this.imageGallery.size(); i++) {
            this.imageGallery.get(i).setDisplayOrder(i + 1);
        }
    }

    @Override
    public String toString() {
        return "Produit{id=" + idProduit + ", nom=" + nomProduit + ", description=" + descriptionProduit + ", prix=" + prixProduit + ", dateCreation=" + dateCreation + "}";
    }
}