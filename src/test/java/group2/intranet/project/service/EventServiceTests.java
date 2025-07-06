package group2.intranet.project.service;

import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.domain.entities.Event;
import group2.intranet.project.mappers.EventMapper;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
import group2.intranet.project.repositories.EventRepository;
import group2.intranet.project.services.EventServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private EventMapper eventMapper; // Real mapper, not mocked

    private EventServiceImpl eventService;

    @BeforeEach
    void setup() {
        // Use the real MapStruct mapper
        eventMapper = Mappers.getMapper(EventMapper.class);
        // Manually inject dependencies
        eventService = new EventServiceImpl(eventRepository, eventMapper);
    }

    @Test
    public void EventService_CreateEvent_ReturnsEventDto() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);
        LocalDate testLocalDate = dateTime.toLocalDate();

        Department testDepartment = Department.builder()
                .id(1)
                .name("Test Department")
                .location("Test Location")
                .email("test@department.com")
                .build();

        Employee testEmployee = Employee.builder()
                .id(100)
                .email("test@test.com")
                .passwordHash("test")
                .firstName("test")
                .lastName("test")
                .phoneNumber("+905350000000")
                .department(testDepartment)
                .jobTitle("Test Job Title")
                .dateOfJoining(testLocalDate)
                .birthday(testLocalDate)
                .role("EMPLOYEE")
                .createdAt(dateTime)
                .build();

        Event event = Event.builder()
                .id(999)
                .title("Test Event")
                .description("Test Description")
                .eventType("Meeting")
                .maxParticipants(50)
                .status("Active")
                .startTime(dateTime.plusHours(1))
                .endTime(dateTime.plusHours(3))
                .location("Test Location")
                .isApproved(true)
                .createdBy(testEmployee)
                .build();

        EventDto eventDto = EventDto.builder()
                .title("Test Event")
                .description("Test Description")
                .eventType("Meeting")
                .maxParticipants(50)
                .status("Active")
                .startTime(dateTime.plusHours(1))
                .endTime(dateTime.plusHours(3))
                .location("Test Location")
                .isApproved(true)
                .createdById(testEmployee.getId())
                .build();

        // Mocks - Only mock the repository, use real mapper
        when(eventRepository.save(Mockito.any(Event.class))).thenReturn(event);

        // Act
        EventDto savedEvent = eventService.createEvent(eventDto);

        // Assert
        Assertions.assertThat(savedEvent).isNotNull();
        Assertions.assertThat(savedEvent.getTitle()).isEqualTo("Test Event");
        Assertions.assertThat(savedEvent.getDescription()).isEqualTo("Test Description");
        Assertions.assertThat(savedEvent.getEventType()).isEqualTo("Meeting");
        Assertions.assertThat(savedEvent.getMaxParticipants()).isEqualTo(50);
        Assertions.assertThat(savedEvent.getLocation()).isEqualTo("Test Location");
    }

    @Test
    public void EventService_GetAllEvents_ReturnsEventDtoList() {
        // Arrange
        Event event1 = Event.builder()
                .id(1)
                .title("Event 1")
                .description("Description 1")
                .eventType("Meeting")
                .build();
                
        Event event2 = Event.builder()
                .id(2)
                .title("Event 2")
                .description("Description 2")
                .eventType("Workshop")
                .build();

        List<Event> events = List.of(event1, event2);

        // Mocks
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        List<EventDto> result = eventService.getAllEvents();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("Event 1");
        Assertions.assertThat(result.get(1).getTitle()).isEqualTo("Event 2");
    }

    @Test
    public void EventService_GetEventById_ReturnsEventDto() {
        // Arrange
        Integer eventId = 1;
        Event event = Event.builder()
                .id(eventId)
                .title("Test Event")
                .description("Test Description")
                .eventType("Meeting")
                .maxParticipants(50)
                .location("Test Location")
                .build();

        // Mocks
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        EventDto result = eventService.getEventById(eventId);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Test Event");
        Assertions.assertThat(result.getDescription()).isEqualTo("Test Description");
        Assertions.assertThat(result.getEventType()).isEqualTo("Meeting");
    }

    @Test
    public void EventService_GetEventById_ReturnsNullWhenNotFound() {
        // Arrange
        Integer eventId = 999;

        // Mocks
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act
        EventDto result = eventService.getEventById(eventId);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void EventService_UpdateEvent_ReturnsUpdatedEventDto() {
        // Arrange
        Integer eventId = 1;
        Event existingEvent = Event.builder()
                .id(eventId)
                .title("Original Title")
                .description("Original Description")
                .eventType("Meeting")
                .maxParticipants(50)
                .location("Original Location")
                .build();

        EventDto updateDto = EventDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .eventType("Workshop")
                .maxParticipants(100)
                .location("Updated Location")
                .build();

        Event updatedEvent = Event.builder()
                .id(eventId)
                .title("Updated Title")
                .description("Updated Description")
                .eventType("Workshop")
                .maxParticipants(100)
                .location("Updated Location")
                .build();

        // Mocks
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(Mockito.any(Event.class))).thenReturn(updatedEvent);

        // Act
        EventDto result = eventService.updateEvent(eventId, updateDto);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Updated Title");
        Assertions.assertThat(result.getDescription()).isEqualTo("Updated Description");
        Assertions.assertThat(result.getEventType()).isEqualTo("Workshop");
        Assertions.assertThat(result.getMaxParticipants()).isEqualTo(100);
        Assertions.assertThat(result.getLocation()).isEqualTo("Updated Location");
    }

    @Test
    public void EventService_UpdateEvent_ReturnsNullWhenNotFound() {
        // Arrange
        Integer eventId = 999;
        EventDto updateDto = EventDto.builder()
                .title("Updated Title")
                .build();

        // Mocks
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act
        EventDto result = eventService.updateEvent(eventId, updateDto);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void EventService_DeleteEvent_CallsRepositoryDelete() {
        // Arrange
        Integer eventId = 1;

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        verify(eventRepository, times(1)).deleteById(eventId);
    }
}
