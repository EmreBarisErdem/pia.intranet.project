package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping(path = "/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable("id") @Min(value = 1,  message = "ID must be greater than or equal to 1") Integer eventId){
        EventDto event = eventService.getEventById(eventId);

        if(event == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }

        return ResponseEntity.ok(event); //200 OK
    }

    @PostMapping(path = "/create")
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventDto dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            Employee loggedInEmployee = (Employee) auth.getPrincipal();
            Integer id = loggedInEmployee.getId();
            dto.setCreatedById(Math.toIntExact(id));

            if (role == null) {
                log.warning("ROLE RETURNED NULL");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Sadece HR isApproved gönderebilir
            if (!"ROLE_HR".equals(role) && Boolean.TRUE.equals(dto.getIsApproved())) {
                log.warning("EMPLOYEE CAN NOT APPROVE THE EVENT ");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Eğer rol HR değilse isApproved false yapılır
            dto.setIsApproved("ROLE_HR".equals(role));

            EventDto createdEvent = eventService.createEvent(dto);
            return createdEvent != null
                    ? ResponseEntity.status(HttpStatus.CREATED).body(createdEvent)
                    : ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.warning("Exception while creating event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping(path = "/update/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventToBeUpdated){

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            Employee loggedInEmployee = (Employee) auth.getPrincipal();
            id = loggedInEmployee.getId();

            eventToBeUpdated.setCreatedById(Math.toIntExact(id));

            if (role == null) {
                log.warning("ROLE RETURNED NULL");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Eğer rol HR değilse isApproved false yapılır, HR ise true olur.
            eventToBeUpdated.setIsApproved("ROLE_HR".equals(role));

            // Sadece HR isApproved = true post edebilir. Employee bir şekilde true gönderse bile UNAUTHORIZED alır.
            if (!"ROLE_HR".equals(role) && Boolean.TRUE.equals(eventToBeUpdated.getIsApproved())) {
                log.warning("EMPLOYEE CAN NOT APPROVE THE EVENT ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            EventDto existingEvent = eventService.getEventById(id);

            if (existingEvent == null){
                log.warning("Event to be updated not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            EventDto updatedEvent = eventService.updateEvent(id,eventToBeUpdated);

            if(updatedEvent == null){
                log.warning("Event not updated with ID: " + id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok(updatedEvent);

        } catch (Exception e) {
            log.warning(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping(path = "/delete/{id}")
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
