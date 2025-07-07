package group2.intranet.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group2.intranet.project.controllers.AnnouncementController;
import group2.intranet.project.domain.dtos.AnnouncementDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.AnnouncementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(AnnouncementController.class)
@Import({TestSecurityConfig.class})
public class AnnouncementControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnouncementService announcementService;

    @Autowired
    private ObjectMapper objectMapper;

    private AnnouncementDTO announcementDto1;
    private AnnouncementDTO announcementDto2;

    @BeforeEach
    void setup() {
        announcementDto1 = AnnouncementDTO.builder()
                .id(1)
                .title("Important Announcement")
                .content("This is an important announcement for all employees.")
                .createdAt(LocalDateTime.of(2025, 1, 15, 9, 0))
                .createdById(1)
                .createdByName("John Admin")
                .build();

        announcementDto2 = AnnouncementDTO.builder()
                .id(2)
                .title("Company Update")
                .content("Company update regarding new policies.")
                .createdAt(LocalDateTime.of(2025, 1, 16, 10, 30))
                .createdById(2)
                .createdByName("Jane Manager")
                .build();
    }

    private void setupAuthenticationWithUserId(Long userId) {
        Employee mockEmployee = new Employee();
        mockEmployee.setId(Math.toIntExact(userId));
        mockEmployee.setEmail("test@company.com");
        mockEmployee.setFirstName("Test");
        mockEmployee.setLastName("User");
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            mockEmployee, 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_HR"))
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void AnnouncementController_GetAll_ReturnsAnnouncementList() throws Exception {
        // Arrange
        List<AnnouncementDTO> announcements = Arrays.asList(announcementDto1, announcementDto2);
        when(announcementService.getAll()).thenReturn(announcements);

        // Act & Assert
        mockMvc.perform(get("/announcements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Important Announcement"))
                .andExpect(jsonPath("$[0].content").value("This is an important announcement for all employees."))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Company Update"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void AnnouncementController_GetAll_ReturnsNoContent_WhenNoAnnouncements() throws Exception {
        // Arrange
        when(announcementService.getAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/announcements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void AnnouncementController_GetById_ReturnsAnnouncement() throws Exception {
        // Arrange
        Integer announcementId = 1;
        when(announcementService.getById(announcementId)).thenReturn(announcementDto1);

        // Act & Assert
        mockMvc.perform(get("/announcements/{id}", announcementId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Important Announcement"))
                .andExpect(jsonPath("$.content").value("This is an important announcement for all employees."))
                .andExpect(jsonPath("$.createdByName").value("John Admin"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void AnnouncementController_GetById_ReturnsNotFound_WhenAnnouncementNotFound() throws Exception {
        // Arrange
        Integer announcementId = 999;
        when(announcementService.getById(announcementId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/announcements/{id}", announcementId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void AnnouncementController_GetById_ThrowsConstraintViolationException_WhenInvalidId() throws Exception {
        // Act & Assert - expecting ServletException wrapping ConstraintViolationException when validation fails
        try {
            mockMvc.perform(get("/announcements/{id}", 0)
                            .contentType(MediaType.APPLICATION_JSON));
            // If we get here, the test should fail
            org.junit.jupiter.api.Assertions.fail("Expected ServletException to be thrown");
        } catch (jakarta.servlet.ServletException e) {
            // Verify that the cause is ConstraintViolationException
            assertTrue(e.getCause() instanceof jakarta.validation.ConstraintViolationException);
            
            // Verify the error message
            jakarta.validation.ConstraintViolationException constraintEx = 
                (jakarta.validation.ConstraintViolationException) e.getCause();
            assertTrue(constraintEx.getMessage().contains("must be greater than or equal to 1"));
        }
    }

    @Test
    public void AnnouncementController_CreateAnnouncement_ReturnsCreatedAnnouncement() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L);
        
        AnnouncementDTO newAnnouncement = AnnouncementDTO.builder()
                .title("New Announcement")
                .content("This is a new announcement.")
                .createdById(1)
                .build();

        AnnouncementDTO createdAnnouncement = AnnouncementDTO.builder()
                .id(3)
                .title("New Announcement")
                .content("This is a new announcement.")
                .createdAt(LocalDateTime.now())
                .createdById(1)
                .createdByName("Test User")
                .build();

        when(announcementService.create(any(AnnouncementDTO.class))).thenReturn(createdAnnouncement);

        // Act & Assert
        mockMvc.perform(post("/announcements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAnnouncement)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("New Announcement"))
                .andExpect(jsonPath("$.content").value("This is a new announcement."));
    }

    @Test
    public void AnnouncementController_CreateAnnouncement_ReturnsCreatedAnnouncement_WhenTitleIsNull() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L);
        
        AnnouncementDTO announcementWithNullTitle = AnnouncementDTO.builder()
                .content("Content without title")
                .build();

        AnnouncementDTO createdAnnouncement = AnnouncementDTO.builder()
                .id(3)
                .content("Content without title")
                .createdAt(LocalDateTime.now())
                .createdById(1)
                .createdByName("Test User")
                .build();

        when(announcementService.create(any(AnnouncementDTO.class))).thenReturn(createdAnnouncement);

        // Act & Assert
        mockMvc.perform(post("/announcements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(announcementWithNullTitle)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.content").value("Content without title"));
    }

    @Test
    public void AnnouncementController_UpdateAnnouncement_ReturnsUpdatedAnnouncement() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L);
        
        Integer announcementId = 1;
        AnnouncementDTO updateRequest = AnnouncementDTO.builder()
                .title("Updated Announcement")
                .content("This is an updated announcement.")
                .build();

        AnnouncementDTO updatedAnnouncement = AnnouncementDTO.builder()
                .id(1)
                .title("Updated Announcement")
                .content("This is an updated announcement.")
                .createdAt(LocalDateTime.of(2025, 1, 15, 9, 0))
                .createdById(1)
                .createdByName("John Admin")
                .build();

        when(announcementService.getById(announcementId)).thenReturn(announcementDto1);
        when(announcementService.update(eq(announcementId), any(AnnouncementDTO.class))).thenReturn(updatedAnnouncement);

        // Act & Assert
        mockMvc.perform(put("/announcements/update/{id}", announcementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Announcement"))
                .andExpect(jsonPath("$.content").value("This is an updated announcement."));
    }

    @Test
    public void AnnouncementController_UpdateAnnouncement_ReturnsNotFound_WhenAnnouncementNotExists() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L);
        
        Integer announcementId = 999;
        AnnouncementDTO updateRequest = AnnouncementDTO.builder()
                .title("Updated Announcement")
                .content("Updated content")
                .build();

        when(announcementService.getById(announcementId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/announcements/update/{id}", announcementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void AnnouncementController_DeleteAnnouncement_ReturnsNoContent() throws Exception {
        // Arrange
        Integer announcementId = 1;
        when(announcementService.getById(announcementId)).thenReturn(announcementDto1);
        doNothing().when(announcementService).delete(announcementId);

        // Act & Assert
        mockMvc.perform(delete("/announcements/delete/{id}", announcementId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void AnnouncementController_DeleteAnnouncement_ReturnsNotFound_WhenAnnouncementNotExists() throws Exception {
        // Arrange
        Integer announcementId = 999;
        when(announcementService.getById(announcementId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(delete("/announcements/delete/{id}", announcementId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void AnnouncementController_GetAll_ReturnsUnauthorized_WhenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/announcements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void AnnouncementController_CreateAnnouncement_ReturnsUnauthorized_WhenNotAuthenticated() throws Exception {
        // Arrange
        AnnouncementDTO newAnnouncement = AnnouncementDTO.builder()
                .title("Test")
                .content("Test content")
                .build();

        // Act & Assert
        mockMvc.perform(post("/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAnnouncement)))
                .andExpect(status().isForbidden());
    }
}
