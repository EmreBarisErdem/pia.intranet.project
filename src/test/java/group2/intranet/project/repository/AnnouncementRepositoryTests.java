package group2.intranet.project.repository;

import group2.intranet.project.domain.entities.Announcement;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.repositories.AnnouncementRepository;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.repositories.EmployeeRepository;
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
public class AnnouncementRepositoryTests {
    private AnnouncementRepository announcementRepository;
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private Employee testEmployee;
    private Department testDepartment;

    String str = "2025-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    LocalDate testLocalDate = LocalDate.parse("2025-04-08 12:30", formatter);

    @Autowired
    public AnnouncementRepositoryTests(AnnouncementRepository announcementRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.announcementRepository = announcementRepository;
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

    private Announcement createAndSaveTestAnnouncement(Integer number) {
        Announcement announcement = Announcement.builder()
                .title("Test Announcement " + number)
                .content("Test Content " + number)
                .publicAnnouncement(true)
                .expiresAt(dateTime.plusDays(30))
                .status("Active")
                .createdBy(testEmployee)
                .build();
        return announcementRepository.save(announcement);
    }

    @Test
    public void AnnouncementRepository_SaveAll_ReturnsSavedAnnouncement() {
        Announcement savedTestAnnouncement = createAndSaveTestAnnouncement(1);

        Assertions.assertThat(savedTestAnnouncement).isNotNull();
        Assertions.assertThat(savedTestAnnouncement.getId()).isGreaterThan(0);
    }

    @Test
    public void AnnouncementRepository_GetAll_ReturnsMoreThenOneAnnouncement() {
        createAndSaveTestAnnouncement(1);
        createAndSaveTestAnnouncement(2);

        List<Announcement> announcementList = announcementRepository.findAll();

        Assertions.assertThat(announcementList).isNotNull();
        Assertions.assertThat(announcementList.size()).isEqualTo(2);
    }

    @Test
    public void AnnouncementRepository_FindById_ReturnsSavedAnnouncement() {
        Announcement savedTestAnnouncement = createAndSaveTestAnnouncement(1);

        Announcement announcementReturn = announcementRepository.findById(savedTestAnnouncement.getId()).get();

        Assertions.assertThat(announcementReturn).isNotNull();
    }

    @Test
    public void AnnouncementRepository_UpdateAnnouncement_ReturnAnnouncement() {
        Announcement savedTestAnnouncement = createAndSaveTestAnnouncement(1);

        Announcement announcementSave = announcementRepository.findById(savedTestAnnouncement.getId()).get();

        announcementSave.setTitle("Updated Announcement Title");
        announcementSave.setContent("Updated Content");
        announcementSave.setPublicAnnouncement(false);
        announcementSave.setStatus("Inactive");
        announcementSave.setExpiresAt(dateTime.plusDays(60));

        Announcement updatedAnnouncement = announcementRepository.save(announcementSave);

        Assertions.assertThat(updatedAnnouncement.getTitle()).isNotNull();
        Assertions.assertThat(updatedAnnouncement.getContent()).isNotNull();
        Assertions.assertThat(updatedAnnouncement.getStatus()).isNotNull();
        Assertions.assertThat(updatedAnnouncement.getExpiresAt()).isNotNull();
        Assertions.assertThat(updatedAnnouncement.getTitle()).isEqualTo("Updated Announcement Title");
        Assertions.assertThat(updatedAnnouncement.isPublicAnnouncement()).isFalse();
    }

    @Test
    public void AnnouncementRepository_AnnouncementDelete_ReturnAnnouncementIsEmpty() {
        Announcement savedTestAnnouncement = createAndSaveTestAnnouncement(1);

        announcementRepository.deleteById(savedTestAnnouncement.getId());
        Optional<Announcement> announcementReturn = announcementRepository.findById(savedTestAnnouncement.getId());

        Assertions.assertThat(announcementReturn).isEmpty();
    }
}
