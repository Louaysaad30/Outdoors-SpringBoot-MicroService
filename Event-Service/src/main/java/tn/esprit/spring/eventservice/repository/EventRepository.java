package tn.esprit.spring.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.eventservice.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Add custom queries if needed
}