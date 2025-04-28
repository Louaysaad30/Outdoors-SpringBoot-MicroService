package tn.esprit.spring.campingservice.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idReservation;

    Date dateDebut;
    Date dateFin;
    int nbrPersonnes;
    float prixCamping;
    float prixLigne;
    float prixTotal;
    Long idClient;
    boolean isConfirmed;
     String paymentIntentId;



    @ManyToOne
    private CentreCamping centre;

    @JsonManagedReference
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<LigneReservation> lignesReservation;


}
