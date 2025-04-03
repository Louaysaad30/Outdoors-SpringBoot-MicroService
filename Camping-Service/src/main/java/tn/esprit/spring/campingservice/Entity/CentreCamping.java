package tn.esprit.spring.campingservice.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    String latitude;
    String longitude; ;
    String address;

    String name;
    int capcite;
    @Lob
    String image;

    Long idOwner;
    float prixJr;
    boolean isVerified;

    @JsonIgnore
    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL)
    private List<Logement> logements;

    @JsonIgnore
    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL)
    private List<Materiel> materiels;

    @JsonIgnore
    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL)
    private List<Reservation> reservations;



}
