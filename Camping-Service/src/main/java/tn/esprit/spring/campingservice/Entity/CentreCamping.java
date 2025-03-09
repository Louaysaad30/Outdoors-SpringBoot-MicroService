package tn.esprit.spring.campingservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CentreCamping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idCentre;

    String address;
    String name;
    int capcite;
    @Lob
    String image;

    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL)
    private List<Logement> logements;

    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL)
    private List<Materiel> materiels;

    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL)
    private List<Reservation> reservations;



}
