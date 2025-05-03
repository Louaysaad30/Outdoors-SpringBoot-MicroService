package tn.esprit.spring.formationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import tn.esprit.spring.formationservice.enums.TypeSponsor;
import tn.esprit.spring.formationservice.enums.NiveauSponsor;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nom;

    @Email
    private String contactEmail;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Numéro invalide (ex: +21612345678)")
    private String telephone; // ✅ format international

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    private TypeSponsor typeSponsor;

    @Enumerated(EnumType.STRING)
    private NiveauSponsor niveauSponsor;

    private String pays;     // si Individu
    private String adresse;  // si Entreprise

    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("sponsor")
    private List<Formation> formations;
}
