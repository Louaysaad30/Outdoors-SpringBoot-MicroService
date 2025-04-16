package tn.esprit.spring.marketplaceservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idImage;

    String imageUrl;

    Integer displayOrder;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "idProduit")
    Produit produit;
}