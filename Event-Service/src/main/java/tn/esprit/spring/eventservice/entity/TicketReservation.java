package tn.esprit.spring.eventservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Random;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private Long userId;

    private String reservationCode;

    @ManyToOne
    @JsonIgnoreProperties({"reservations"})
    private Ticket ticket;


    private String appliedDiscountCode;
    private Double finalPrice;

}