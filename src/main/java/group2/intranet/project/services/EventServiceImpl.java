package group2.intranet.project.services;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.domain.entities.Event;
import group2.intranet.project.mappers.EventMapper;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
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

    public EventServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, EventMapper eventMapper, EventRepository eventRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
    }

    private EventRepository eventRepository;
    private EventMapper eventMapper;
    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;



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
    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        try {
            Event eventEntity = eventMapper.toEntity(eventDto);

            // createdBy set et
            Employee creator = employeeRepository.findById(Long.valueOf(eventDto.getCreatedById()))
                    .orElse(null);

            eventEntity.setCreatedBy(creator);

            List<Department> eventDepartments = departmentRepository.findAllById(eventDto.getDepartmentIds());

            // 2. Departmentları set et ve tekrar kaydet
            eventEntity.setDepartments(eventDepartments);

            // 1. Event'i kaydet (Hep creator hem de departmanları ile

            Event savedEventEntity = eventRepository.saveAndFlush(eventEntity);

            return eventMapper.toDTO(savedEventEntity);


        } catch (Exception e) {
            log.warning("Error while creating event: " + e.getMessage());
            return null;
        }
    }


    @Override
    public EventDto updateEvent(Integer id, EventDto updatedEvent) {
        try {
            Event existingEvent = eventRepository.findById(id).orElse(null);

            eventMapper.updateEventFromDto(updatedEvent,existingEvent);

            assert existingEvent != null;

            // createdBy set et
            Employee creator = employeeRepository.findById(Long.valueOf(updatedEvent.getCreatedById()))
                    .orElse(null);

            existingEvent.setCreatedBy(creator);

            List<Department> eventDepartments = departmentRepository.findAllById(updatedEvent.getDepartmentIds());
            // 2. Departmentları set et ve tekrar kaydet
            existingEvent.setDepartments(eventDepartments);


            Event savedEvent = eventRepository.saveAndFlush(existingEvent);

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
