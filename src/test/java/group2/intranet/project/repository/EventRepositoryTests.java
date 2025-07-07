package group2.intranet.project.repository;

import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.domain.entities.Event;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
import group2.intranet.project.repositories.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class EventRepositoryTests {
    private EventRepository eventRepository;
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private Employee testEmployee;
    private Department testDepartment;

    String str = "2025-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    LocalDate testLocalDate = LocalDate.parse("2025-04-08 12:30", formatter);

    @Autowired
    public EventRepositoryTests(EventRepository eventRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.eventRepository = eventRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @BeforeEach
    public void setup() {
        testDepartment = departmentRepository.save(
                Department.builder()
                        .name("Test Department")
                        .location("Test Location")
                        .email("test@department.com")
                        .build()
        );

        testEmployee = employeeRepository.save(
                Employee.builder()
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
                        .build()
        );
    }

    private Event createAndSaveTestEvent(Integer number) {
        Event event = Event.builder()
                .title("Test Event " + number)
                .description("Test Description " + number)
                .eventType("Meeting")
                .maxParticipants(50)
                .status("Active")
                .startTime(dateTime.plusHours(1))
                .endTime(dateTime.plusHours(3))
                .location("Test Location " + number)
                .isApproved(true)
                .createdBy(testEmployee)
                .build();
        return eventRepository.save(event);
    }

    @Test
    public void EventRepository_SaveAll_ReturnsSavedEvent() {
        Event savedTestEvent = createAndSaveTestEvent(1);

        Assertions.assertThat(savedTestEvent).isNotNull();
        Assertions.assertThat(savedTestEvent.getId()).isGreaterThan(0);
    }

    @Test
    public void EventRepository_GetAll_ReturnsMoreThenOneEvent() {
        createAndSaveTestEvent(1);
        createAndSaveTestEvent(2);

        List<Event> eventList = eventRepository.findAll();

        Assertions.assertThat(eventList).isNotNull();
        Assertions.assertThat(eventList.size()).isEqualTo(2);
    }

    @Test
    public void EventRepository_FindById_ReturnsSavedEvent() {
        Event savedTestEvent = createAndSaveTestEvent(1);

        Event eventReturn = eventRepository.findById(savedTestEvent.getId()).get();

        Assertions.assertThat(eventReturn).isNotNull();
    }

    @Test
    public void EventRepository_UpdateEvent_ReturnEvent() {
        Event savedTestEvent = createAndSaveTestEvent(1);

        Event eventSave = eventRepository.findById(savedTestEvent.getId()).get();

        eventSave.setTitle("Updated Event Title");
        eventSave.setDescription("Updated Description");
        eventSave.setEventType("Workshop");
        eventSave.setMaxParticipants(100);
        eventSave.setLocation("Updated Location");
        eventSave.setIsApproved(false);

        Event updatedEvent = eventRepository.save(eventSave);

        Assertions.assertThat(updatedEvent.getTitle()).isNotNull();
        Assertions.assertThat(updatedEvent.getDescription()).isNotNull();
        Assertions.assertThat(updatedEvent.getEventType()).isNotNull();
        Assertions.assertThat(updatedEvent.getMaxParticipants()).isNotNull();
        Assertions.assertThat(updatedEvent.getLocation()).isNotNull();
        Assertions.assertThat(updatedEvent.getIsApproved()).isNotNull();
        Assertions.assertThat(updatedEvent.getTitle()).isEqualTo("Updated Event Title");
    }

    @Test
    public void EventRepository_EventDelete_ReturnEventIsEmpty() {
        Event savedTestEvent = createAndSaveTestEvent(1);

        eventRepository.deleteById(savedTestEvent.getId());
        Optional<Event> eventReturn = eventRepository.findById(savedTestEvent.getId());

        Assertions.assertThat(eventReturn).isEmpty();
    }
}
