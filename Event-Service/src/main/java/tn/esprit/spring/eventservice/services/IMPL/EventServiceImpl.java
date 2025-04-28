package tn.esprit.spring.eventservice.services.IMPL;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.spring.eventservice.entity.Event;
import tn.esprit.spring.eventservice.entity.Status;
import tn.esprit.spring.eventservice.repository.EventRepository;
import tn.esprit.spring.eventservice.services.interfaces.IEventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updatePastEventsStatus() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Running scheduled event status update task at {}", now);

        // Find events that have ended and are not already FINISHED or CANCELED
        List<Event> eventsToUpdate = eventRepository.findAll().stream()
                .filter(event -> event.getEndDate().isBefore(now))
                .filter(event -> event.getStatus() != Status.FINISHED && event.getStatus() != Status.CANCELED)
                .toList();

        if (!eventsToUpdate.isEmpty()) {
            log.info("Found {} events to mark as FINISHED", eventsToUpdate.size());

            for (Event event : eventsToUpdate) {
                event.setStatus(Status.FINISHED);
                log.debug("Marking event {} as FINISHED", event.getId());
            }

            eventRepository.saveAll(eventsToUpdate);
            log.info("Successfully updated {} events to FINISHED status", eventsToUpdate.size());
        }
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}