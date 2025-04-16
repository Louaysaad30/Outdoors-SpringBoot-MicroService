package tn.esprit.spring.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.eventservice.entity.EventArea;

@Repository
public interface EventAreaRepository extends JpaRepository<EventArea, Long> {

}