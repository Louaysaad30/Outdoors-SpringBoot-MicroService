package tn.esprit.spring.userservice.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    int sessions = 0;
    int totalSessions = 0;
    int nDaysAfterOnboarding = 0;
    int totalNavigationsFav1 = 0;
    int activityDays = 0;

    @Column(updatable = false)
    LocalDateTime createdAt;

    // 👇 New field added to track the last session month
    String lastSessionMonth; // Format: "2025-04"
    LocalDate lastActiveDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
