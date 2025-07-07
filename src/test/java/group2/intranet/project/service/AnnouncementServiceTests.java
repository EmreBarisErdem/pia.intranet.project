package group2.intranet.project.service;

import group2.intranet.project.domain.dtos.AnnouncementDTO;
import group2.intranet.project.domain.entities.Announcement;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.mappers.AnnouncementMapper;
import group2.intranet.project.repositories.AnnouncementRepository;
import group2.intranet.project.services.AnnouncementServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AnnouncementServiceTests {

    @Mock
    private AnnouncementRepository announcementRepository;

    private AnnouncementMapper announcementMapper; // Real mapper, not mocked

    private AnnouncementServiceImpl announcementService;

    @BeforeEach
    void setup() {
        // Use the real MapStruct mapper
        announcementMapper = Mappers.getMapper(AnnouncementMapper.class);
        // Manually inject dependencies
        announcementService = new AnnouncementServiceImpl(announcementRepository, announcementMapper);
    }

    @Test
    public void AnnouncementService_GetAll_ReturnsAnnouncementDTOList() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee createdBy = Employee.builder().id(100).build();

        Announcement announcement1 = Announcement.builder()
                .id(1)
                .title("Announcement 1")
                .content("Content 1")
                .publicAnnouncement(true)
                .status("Active")
                .createdAt(dateTime)
                .createdBy(createdBy)
                .build();

        Announcement announcement2 = Announcement.builder()
                .id(2)
                .title("Announcement 2")
                .content("Content 2")
                .publicAnnouncement(false)
                .status("Draft")
                .createdAt(dateTime)
                .createdBy(createdBy)
                .build();

        List<Announcement> announcements = List.of(announcement1, announcement2);

        // Mocks
        when(announcementRepository.findAll()).thenReturn(announcements);

        // Act
        List<AnnouncementDTO> result = announcementService.getAll();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("Announcement 1");
        Assertions.assertThat(result.get(0).isPublicAnnouncement()).isTrue();
        Assertions.assertThat(result.get(1).getTitle()).isEqualTo("Announcement 2");
        Assertions.assertThat(result.get(1).isPublicAnnouncement()).isFalse();
    }

    @Test
    public void AnnouncementService_GetById_ReturnsAnnouncementDTO() {
        // Arrange
        Integer announcementId = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee createdBy = Employee.builder().id(100).build();

        Announcement announcement = Announcement.builder()
                .id(announcementId)
                .title("Test Announcement")
                .content("Test Content")
                .publicAnnouncement(true)
                .status("Active")
                .createdAt(dateTime)
                .createdBy(createdBy)
                .build();

        // Mocks
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));

        // Act
        AnnouncementDTO result = announcementService.getById(announcementId);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Test Announcement");
        Assertions.assertThat(result.getContent()).isEqualTo("Test Content");
        Assertions.assertThat(result.isPublicAnnouncement()).isTrue();
        Assertions.assertThat(result.getStatus()).isEqualTo("Active");
    }

    @Test
    public void AnnouncementService_GetById_ThrowsExceptionWhenNotFound() {
        // Arrange
        Integer announcementId = 999;

        // Mocks
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> announcementService.getById(announcementId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Announcement not found with id: " + announcementId);
    }

    @Test
    public void AnnouncementService_Create_ReturnsCreatedAnnouncementDTO() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee createdBy = Employee.builder()
                .id(100)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .build();

        AnnouncementDTO announcementDTO = AnnouncementDTO.builder()
                .title("New Announcement")
                .content("New Content")
                .publicAnnouncement(true)
                .status("Active")
                .build();

        Announcement savedAnnouncement = Announcement.builder()
                .id(1)
                .title("New Announcement")
                .content("New Content")
                .publicAnnouncement(true)
                .status("Active")
                .createdAt(dateTime)
                .createdBy(createdBy) // Add the createdBy employee
                .build();

        // Mocks
        when(announcementRepository.save(Mockito.any(Announcement.class))).thenReturn(savedAnnouncement);

        // Act
        AnnouncementDTO result = announcementService.create(announcementDTO);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("New Announcement");
        Assertions.assertThat(result.getContent()).isEqualTo("New Content");
        Assertions.assertThat(result.isPublicAnnouncement()).isTrue();
        Assertions.assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    public void AnnouncementService_Update_ReturnsUpdatedAnnouncementDTO() {
        // Arrange
        Integer announcementId = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Employee createdBy = Employee.builder()
                .id(100)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .build();

        Announcement existingAnnouncement = Announcement.builder()
                .id(announcementId)
                .title("Original Title")
                .content("Original Content")
                .publicAnnouncement(true)
                .status("Active")
                .createdAt(dateTime)
                .createdBy(createdBy)
                .build();

        AnnouncementDTO updateDTO = AnnouncementDTO.builder()
                .title("Updated Title")
                .content("Updated Content")
                .publicAnnouncement(false)
                .status("Draft")
                .build();

        Announcement updatedAnnouncement = Announcement.builder()
                .id(announcementId)
                .title("Updated Title")
                .content("Updated Content")
                .publicAnnouncement(false)
                .status("Draft")
                .createdAt(dateTime)
                .createdBy(createdBy) // Add the createdBy employee
                .build();

        // Mocks
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(existingAnnouncement));
        when(announcementRepository.save(Mockito.any(Announcement.class))).thenReturn(updatedAnnouncement);

        // Act
        AnnouncementDTO result = announcementService.update(announcementId, updateDTO);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Updated Title");
        Assertions.assertThat(result.getContent()).isEqualTo("Updated Content");
        Assertions.assertThat(result.isPublicAnnouncement()).isFalse();
        Assertions.assertThat(result.getStatus()).isEqualTo("Draft");
    }

    @Test
    public void AnnouncementService_Update_ThrowsExceptionWhenNotFound() {
        // Arrange
        Integer announcementId = 999;
        AnnouncementDTO updateDTO = AnnouncementDTO.builder()
                .title("Updated Title")
                .build();

        // Mocks
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> announcementService.update(announcementId, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Announcement not found with id: " + announcementId);
    }

    @Test
    public void AnnouncementService_Delete_CallsRepositoryDelete() {
        // Arrange
        Integer announcementId = 1;

        // Act
        announcementService.delete(announcementId);

        // Assert
        verify(announcementRepository, times(1)).deleteById(announcementId);
    }
}
