package tn.esprit.spring.eventservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.spring.eventservice.entity.Status;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String imageUrl;

    @ManyToOne
    //@JoinColumn(name = "event_area_id")
    private EventArea eventArea;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
   // @JsonManagedReference
    @JsonIgnore
    private List<Ticket> tickets;
}
