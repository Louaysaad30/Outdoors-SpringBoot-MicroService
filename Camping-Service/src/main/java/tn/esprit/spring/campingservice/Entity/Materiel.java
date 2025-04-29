package tn.esprit.spring.campingservice.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    String description;
    int quantity;
    float price;

    TypeMateriel type;

    @Lob
    String image;

    @JsonBackReference
    @ManyToOne
    private CentreCamping centre;
}
