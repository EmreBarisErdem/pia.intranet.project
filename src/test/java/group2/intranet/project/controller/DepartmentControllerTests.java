package group2.intranet.project.controller;

import group2.intranet.project.controllers.DepartmentController;
import group2.intranet.project.domain.dtos.DepartmentDTO;
import group2.intranet.project.services.DepartmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DepartmentController.class)
@ContextConfiguration(classes = {DepartmentController.class, DepartmentControllerTests.TestConfig.class})
public class DepartmentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentService departmentService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DepartmentService departmentService() {
            return mock(DepartmentService.class);
        }
    }

    private DepartmentDTO departmentDto1;
    private DepartmentDTO departmentDto2;

    @BeforeEach
    void setup() {
        departmentDto1 = DepartmentDTO.builder()
                .id(1)
                .name("Information Technology")
                .location("Building A, Floor 3")
                .email("it@company.com")
                .createdAt(LocalDateTime.of(2020, 1, 15, 9, 0))
                .build();

        departmentDto2 = DepartmentDTO.builder()
                .id(2)
                .name("Human Resources")
                .location("Building B, Floor 2")
                .email("hr@company.com")
                .createdAt(LocalDateTime.of(2020, 1, 16, 10, 30))
                .build();
    }

    @Test
    @WithMockUser(roles = "HR")
    public void DepartmentController_GetAll_ReturnsDepartmentList() throws Exception {
        // Arrange
        List<DepartmentDTO> departments = Arrays.asList(departmentDto1, departmentDto2);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/departments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Information Technology"))
                .andExpect(jsonPath("$[0].location").value("Building A, Floor 3"))
                .andExpect(jsonPath("$[0].email").value("it@company.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Human Resources"))
                .andExpect(jsonPath("$[1].email").value("hr@company.com"));
    }

    @Test
    @WithMockUser(roles = "HR")
    public void DepartmentController_GetById_ReturnsDepartment() throws Exception {
        // Arrange
        Integer departmentId = 1;
        when(departmentService.getDepartmentById(departmentId)).thenReturn(departmentDto1);

        // Act & Assert
        mockMvc.perform(get("/departments/{id}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Information Technology"))
                .andExpect(jsonPath("$.location").value("Building A, Floor 3"))
                .andExpect(jsonPath("$.email").value("it@company.com"));
    }

    @Test
    @WithMockUser(roles = "HR")
    public void DepartmentController_GetById_ReturnsNoContent_WhenDepartmentNotFound() throws Exception {
        // Arrange
        Integer departmentId = 999;
        when(departmentService.getDepartmentById(departmentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/departments/{id}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
