package tn.esprit.spring.eventservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Event Area representing a venue or location for events")
public class EventArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the event area")
    private Long id;

    @Schema(description = "Name of the venue")
    private String name;

    @Schema(description = "Maximum number of people the venue can hold")
    private Integer capacity;

    @Schema(description = "Geographic latitude coordinate")
    private Double latitude;

    @Schema(description = "Geographic longitude coordinate")
    private Double longitude;

    @Schema(description = "Detailed description of the venue")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Schema(description = "URL or path to the image of the area")
    private String areaImg;

    @OneToMany(mappedBy = "eventArea")
    @JsonIgnore
    @Schema(description = "List of events hosted at this venue")
    private List<Event> events;

    @ElementCollection
    private Set<String> keywords = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private EventAreaStatus status = EventAreaStatus.PENDING;

    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String rejectionMessage;

    // For backward compatibility
    public boolean isApproved() {
        return status == EventAreaStatus.APPROVED;
    }
}