package tn.esprit.spring.eventservice.services.interfaces;

import tn.esprit.spring.eventservice.entity.EventArea;

import java.util.List;
import java.util.Optional;

public interface IEventAreaService {
    List<EventArea> getAllEventAreas();
    Optional<EventArea> getEventAreaById(Long id);
    EventArea saveEventArea(EventArea eventArea);
    EventArea updateEventArea(EventArea eventArea);
    void deleteEventArea(Long id);
    List<EventArea> getApprovedEventAreas();
    List<EventArea> getPendingEventAreas();
    List<EventArea>getRejectedEventAreas();
    List<EventArea> getEventAreasByUserId(Long userId);
}