package tn.esprit.spring.eventservice.services.IMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.eventservice.entity.EventArea;
import tn.esprit.spring.eventservice.repository.EventAreaRepository;
import tn.esprit.spring.eventservice.services.IMPL.EventAreaServiceImpl;
import tn.esprit.spring.eventservice.services.interfaces.IEventAreaService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventAreaServiceImpl implements IEventAreaService {

    private final EventAreaRepository eventAreaRepository;

    @Override
    public List<EventArea> getAllEventAreas() {
        return eventAreaRepository.findAll();
    }

    @Override
    public Optional<EventArea> getEventAreaById(Long id) {
        return eventAreaRepository.findById(id);
    }

    @Override
    public EventArea saveEventArea(EventArea eventArea) {
        return eventAreaRepository.save(eventArea);
    }

    @Override
    public EventArea updateEventArea(EventArea eventArea) {
        return eventAreaRepository.save(eventArea);
    }

    @Override
    public void deleteEventArea(Long id) {
        eventAreaRepository.deleteById(id);
    }
}