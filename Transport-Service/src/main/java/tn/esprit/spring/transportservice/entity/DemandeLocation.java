package tn.esprit.spring.transportservice.entity;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    private Double prixTotal;

    private LocalDateTime DebutLocation;
    private LocalDateTime FinLocation;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    public enum StatutDemande {
        EN_ATTENTE, APPROUVÉE, REJETÉE
    }
}
