package tn.esprit.spring.eventservice.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.spring.eventservice.entity.TicketType;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    private Double price;

    private Integer availableTickets;

    private Integer purchaseLimit;

    private String discountCode;

    @ManyToOne
    private Event event;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketReservation> reservations;
}