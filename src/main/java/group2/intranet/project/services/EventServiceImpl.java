package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Event;
import group2.intranet.project.mappers.EventMapper;
import group2.intranet.project.repositories.EventRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private EventMapper eventMapper;


    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventDto> getAllEvents() {

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEventById(Integer id) {
        Event event = eventRepository.findById(id).orElse(null);

        return eventMapper.toDTO(event);
    }

    @Override
    public EventDto createEvent(EventDto eventDto) {
        try {
            Event eventEntity = eventMapper.toEntity(eventDto);
            Event result = eventRepository.save(eventEntity);

            if (result.getId() == null) {
                log.info("Event creation failed. ID is null after save.");
                return null;
            }

            return eventMapper.toDTO(result);

        } catch (Exception e) {
            log.info("Error while creating event : Error Message" + e.getMessage());
            return null;
        }

    }

    @Override
    public EventDto updateEvent(Integer id, EventDto updatedEvent) {
        try {
            Event existingEvent = eventRepository.findById(id).orElse(null);

            eventMapper.updateEventFromDto(updatedEvent,existingEvent);

            assert existingEvent != null;
            Event savedEvent = eventRepository.save(existingEvent);

            return eventMapper.toDTO(savedEvent);
        }
        catch (Exception e){
            log.info("Event to update not found");
            return null;
        }

    }

    @Override
    public void deleteEvent(Integer id) {
       eventRepository.deleteById(id);
    }
}
