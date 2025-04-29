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
public class CodeProduit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idCodeProduit;
    String code;
    @JsonIgnore
    @OneToMany(mappedBy = "codeProduit")
    List<Produit> produit;
}
