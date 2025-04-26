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
public class Panier {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long idPanier;
        @JsonIgnore
        @OneToMany(mappedBy = "panier")
        private List<LigneCommande> lignesCommande;
        Double total;
        Long userId;
        boolean validated;

        public Long getId() {
                return idPanier;
        }
}
