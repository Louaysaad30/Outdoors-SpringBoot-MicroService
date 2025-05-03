package tn.esprit.spring.transportservice.dto;

import lombok.Data;
import tn.esprit.spring.transportservice.enums.TypeVehicule;

@Data
public class VehiculeDTO {
    private Long id;
    private TypeVehicule type;
    private String modele;
    private String localisation;
    private String description;
    private Double rating;
    private Double prixParJour;
}