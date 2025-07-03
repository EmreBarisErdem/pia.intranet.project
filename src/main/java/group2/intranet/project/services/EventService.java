package group2.intranet.project.services;


import group2.intranet.project.domain.dtos.EventDto;

import java.util.List;

public interface EventService {

    public List<EventDto> getAllEvents();

    public EventDto getEventById(Integer id);

    public EventDto createEvent(EventDto eventDto);

    public EventDto updateEvent(Integer id, EventDto updatedEvent);

    public void deleteEvent(Integer id);

}
