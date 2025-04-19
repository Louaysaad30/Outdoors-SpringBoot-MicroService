package tn.esprit.spring.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.eventservice.entity.Event;
import tn.esprit.spring.eventservice.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEndDateBeforeAndStatusNot(LocalDateTime endDate, Status status);
}