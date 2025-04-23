package tn.esprit.spring.marketplaceservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.core.RepositoryInformation;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class Favoris {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idFavoris;
    Long idUser;
    Long idProduit;




}
