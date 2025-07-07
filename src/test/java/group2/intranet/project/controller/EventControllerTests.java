package group2.intranet.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group2.intranet.project.controllers.EventController;
import group2.intranet.project.domain.dtos.EventDto;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.EventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import({TestSecurityConfig.class})
public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventDto eventDto1;
    private EventDto eventDto2;

    @BeforeEach
    void setup() {
        eventDto1 = EventDto.builder()
                .id(1)
                .title("Company Meeting")
                .description("Monthly company meeting")
                .startTime(LocalDateTime.of(2025, 2, 15, 14, 0))
                .endTime(LocalDateTime.of(2025, 2, 15, 16, 0))
                .location("Conference Room A")
                .isApproved(true)
                .createdById(1)
                .build();

        eventDto2 = EventDto.builder()
                .id(2)
                .title("Team Building")
                .description("Team building activity")
                .startTime(LocalDateTime.of(2025, 2, 20, 10, 0))
                .endTime(LocalDateTime.of(2025, 2, 20, 17, 0))
                .location("Outdoor Park")
                .isApproved(false)
                .createdById(2)
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
    public void EventController_GetAll_ReturnsEventList() throws Exception {
        // Arrange
        List<EventDto> events = Arrays.asList(eventDto1, eventDto2);
        when(eventService.getAllEvents()).thenReturn(events);

        // Act & Assert
        mockMvc.perform(get("/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Company Meeting"))
                .andExpect(jsonPath("$[0].isApproved").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Team Building"))
                .andExpect(jsonPath("$[1].isApproved").value(false));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void EventController_GetAll_ReturnsNoContent_WhenNoEvents() throws Exception {
        // Arrange
        when(eventService.getAllEvents()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void EventController_GetById_ReturnsEvent() throws Exception {
        // Arrange
        Integer eventId = 1;
        when(eventService.getEventById(eventId)).thenReturn(eventDto1);

        // Act & Assert
        mockMvc.perform(get("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Company Meeting"))
                .andExpect(jsonPath("$.description").value("Monthly company meeting"))
                .andExpect(jsonPath("$.location").value("Conference Room A"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void EventController_GetById_ReturnsNotFound_WhenEventNotFound() throws Exception {
        // Arrange
        Integer eventId = 999;
        when(eventService.getEventById(eventId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void EventController_CreateEvent_WithHRRole_ReturnsCreatedEvent() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L);
        
        EventDto newEvent = EventDto.builder()
                .title("New Event")
                .description("New event description")
                .startTime(LocalDateTime.of(2025, 3, 1, 9, 0))
                .endTime(LocalDateTime.of(2025, 3, 1, 11, 0))
                .location("Meeting Room B")
                .isApproved(true)
                .createdById(1)
                .build();

        EventDto createdEvent = EventDto.builder()
                .id(3)
                .title("New Event")
                .description("New event description")
                .startTime(LocalDateTime.of(2025, 3, 1, 9, 0))
                .endTime(LocalDateTime.of(2025, 3, 1, 11, 0))
                .location("Meeting Room B")
                .isApproved(true)
                .createdById(1)
                .build();

        when(eventService.createEvent(any(EventDto.class))).thenReturn(createdEvent);

        // Act & Assert
        mockMvc.perform(post("/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("New Event"))
                .andExpect(jsonPath("$.isApproved").value(true));
    }

    @Test
    public void EventController_GetAll_ReturnsUnauthorized_WhenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void EventController_CreateEvent_ReturnsUnauthorized_WhenNotAuthenticated() throws Exception {
        // Arrange
        EventDto newEvent = EventDto.builder()
                .title("Test Event")
                .description("Test description")
                .build();

        // Act & Assert
        mockMvc.perform(post("/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isForbidden());
    }
}
