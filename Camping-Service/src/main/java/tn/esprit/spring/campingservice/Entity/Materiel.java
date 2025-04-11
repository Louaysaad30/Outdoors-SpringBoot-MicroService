package tn.esprit.spring.campingservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idMateriel;

    String name;
    int quantity;
    float price;

    @Lob
    String image;

    @ManyToOne
    private CentreCamping centre;
}
