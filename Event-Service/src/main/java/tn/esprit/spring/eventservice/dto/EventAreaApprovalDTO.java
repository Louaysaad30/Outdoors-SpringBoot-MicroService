package tn.esprit.spring.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAreaApprovalDTO {
    private String message; // Optional feedback message
}