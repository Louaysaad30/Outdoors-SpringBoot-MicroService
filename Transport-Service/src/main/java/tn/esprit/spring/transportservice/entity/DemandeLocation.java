package tn.esprit.spring.transportservice.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemandeLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String fullName;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    private Double prixTotal;

    private LocalDateTime debutLocation;
    private LocalDateTime finLocation;

    private String pickupLocation;

    private Double pickupLatitude;
    private Double pickupLongitude;


    @Column(length = 255)
    private String causeRejet;



    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    public enum StatutDemande {
        EN_ATTENTE, APPROUVÉE, REJETÉE
    }
}
