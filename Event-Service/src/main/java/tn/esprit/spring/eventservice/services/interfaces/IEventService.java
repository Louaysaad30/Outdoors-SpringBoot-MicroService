package tn.esprit.spring.eventservice.services.interfaces;

import tn.esprit.spring.eventservice.entity.Event;
import java.util.List;
import java.util.Optional;

public interface IEventService {
    List<Event> getAllEvents();
    Optional<Event> getEventById(Long id);
    Event saveEvent(Event event);
    Event updateEvent(Event event);
    void deleteEvent(Long id);
}