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

public class Logement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idLogement;

    String name;
    int quantity;
    float price;
    TypeLogement type;

    @Lob
    String image;

    @ManyToOne
    private CentreCamping centre;
}
