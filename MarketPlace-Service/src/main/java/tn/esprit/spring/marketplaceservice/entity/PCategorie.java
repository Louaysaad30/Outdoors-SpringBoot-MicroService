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
public class PCategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idCategorie;
    String nomCategorie;

    @JsonIgnore
    @OneToMany(mappedBy = "categorie")
    private List<Produit> produit;
}
