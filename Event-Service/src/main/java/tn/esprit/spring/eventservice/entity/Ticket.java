package tn.esprit.spring.eventservice.entity;

import com.fasterxml.jackson.annotation.*;
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
    @Column(length = 20)
    private TicketType type;

    private Double price;

    private Integer availableTickets;

    private Integer purchaseLimit;

    private String discountCode;

    @ManyToOne
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    //@JsonIdentityReference(alwaysAsId = true)
    //@JsonBackReference
    @JsonIgnoreProperties({"tickets"})
    private Event event;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketReservation> reservations;
}