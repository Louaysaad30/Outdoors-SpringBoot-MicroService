package tn.esprit.spring.transportservice.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.transportservice.enums.TypeVehicule;
import tn.esprit.spring.transportservice.enums.StatutVehicule;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeVehicule type;

    private String modele;
    private boolean disponible;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    private String localisation;
    private Double prixParJour;
    private Integer nbPlace;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private Double rating;

    //@Lob
    @Column(nullable = true)
    private String image;

    @ManyToOne
    @JoinColumn(name = "agence_id")
    private Agence agence;


    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Review> reviews;



}
