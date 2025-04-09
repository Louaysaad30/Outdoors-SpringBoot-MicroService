package tn.esprit.spring.campingservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LigneReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idLigne;

    Date dateDebut;
    Date dateFin;
    int quantite;
    float prix;

    @ManyToOne
    private Reservation reservation;

    @ManyToOne
    private Logement logement;

    @ManyToOne
    private Materiel materiel;
}
