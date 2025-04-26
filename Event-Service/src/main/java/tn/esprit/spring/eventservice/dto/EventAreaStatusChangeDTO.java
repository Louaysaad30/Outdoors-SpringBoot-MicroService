package tn.esprit.spring.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.spring.eventservice.entity.EventAreaStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAreaStatusChangeDTO {
    private EventAreaStatus newStatus;
    private String message; // For rejection messages or other status change notes
}