package group2.intranet.project.services;


import group2.intranet.project.domain.dtos.EventDto;

import java.util.List;

public interface EventService {

    List<EventDto> getAllEvents();

    EventDto getEventById(Integer id);

    EventDto createEvent(EventDto eventDto);

    EventDto updateEvent(Integer id, EventDto updatedEvent);

    void deleteEvent(Integer id);

}
