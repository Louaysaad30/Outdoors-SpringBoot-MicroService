package tn.esprit.spring.marketplaceservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
        @Enumerated(EnumType.STRING)
        Status etatLivraison;
        Long livreurId;
        String OrderNumber;
        Double montantCommande;
        String paymentMethod;
        LocalDateTime updateDate;


        @JsonIgnore
        @OneToMany(mappedBy = "livraison")
        private List<Commande> commande;
}
