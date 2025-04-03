package tn.esprit.spring.transportservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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


    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    public enum StatutDemande {
        EN_ATTENTE, APPROUVÉE, REJETÉE
    }
}
