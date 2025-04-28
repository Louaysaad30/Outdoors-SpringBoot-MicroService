package tn.esprit.spring.campingservice.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    int numTel;

    @JsonManagedReference
    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private List<Logement> logements;

    @JsonManagedReference
    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private List<Materiel> materiels;

    @JsonIgnore
    @OneToMany(mappedBy = "centre", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Reservation> reservations;



}
