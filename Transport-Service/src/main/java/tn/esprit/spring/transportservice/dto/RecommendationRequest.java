package tn.esprit.spring.transportservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationRequest {
    private String mood_input;
    private List<VehiculeDTO> vehicules;
}