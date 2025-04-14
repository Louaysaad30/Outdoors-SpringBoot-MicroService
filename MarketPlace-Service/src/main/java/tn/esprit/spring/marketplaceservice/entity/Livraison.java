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
public class Livraison {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long idLivraison;
        LocalDate dateLivraison;
        String adresseLivraison;
        Status etatLivraison;

        @JsonIgnore
        @OneToMany(mappedBy = "livraison")
        private List<Commande> commande;
}
