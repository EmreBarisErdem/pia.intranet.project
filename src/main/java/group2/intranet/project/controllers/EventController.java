package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.services.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Log
@RestController
@RequestMapping(path = "/events")
public class EventController {
    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAll(){
        List<EventDto> events = eventService.getAllEvents();

        if (events.isEmpty()){
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(events); // 200 OK
    }

    @GetMapping("{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable("id") @Min(value = 1,  message = "ID must be greater than or equal to 1") Integer eventId){
        EventDto event = eventService.getEventById(eventId);

        if(event == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }

        return ResponseEntity.ok(event); //200 OK
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventDto eventDto) {
        EventDto createdEvent = eventService.createEvent(eventDto);

        if (createdEvent == null) {
            log.warning("Event creation failed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Event created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventToBeUpdated){

        EventDto existingEvent = eventService.getEventById(id);

        if (existingEvent == null){
            log.warning("Event to update not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        EventDto updatedEvent = eventService.updateEvent(id,eventToBeUpdated);

        if(updatedEvent == null){
            log.info("Event not updated with ID: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(updatedEvent);

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Integer id){
        EventDto existingEvent = eventService.getEventById(id);

        if (existingEvent == null) {
            log.warning("Event to delete not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        eventService.deleteEvent(id);

        log.info("Deleted event with ID: " + id);
        return ResponseEntity.ok("Event deleted successfully.");
    }

}
