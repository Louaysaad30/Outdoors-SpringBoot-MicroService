package tn.esprit.spring.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.eventservice.entity.EventArea;
import tn.esprit.spring.eventservice.entity.EventAreaStatus;

import java.util.List;

@Repository
public interface EventAreaRepository extends JpaRepository<EventArea, Long> {
    List<EventArea> findByStatus(EventAreaStatus status);
    List<EventArea> findByUserId(Long userId);
}