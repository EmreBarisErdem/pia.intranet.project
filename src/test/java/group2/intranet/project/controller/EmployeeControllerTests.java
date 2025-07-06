package group2.intranet.project.controller;

import group2.intranet.project.controllers.EmployeeController;
import group2.intranet.project.domain.dtos.EmployeeDTO;
import group2.intranet.project.services.EmployeeService;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
@ContextConfiguration(classes = {EmployeeController.class, EmployeeControllerTests.TestConfig.class})
public class EmployeeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public EmployeeService employeeService() {
            return mock(EmployeeService.class);
        }
    }

    private EmployeeDTO employeeDto1;
    private EmployeeDTO employeeDto2;

    @BeforeEach
    void setup() {
        employeeDto1 = EmployeeDTO.builder()
                .id(1)
                .email("john.doe@test.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123-456-7890")
                .departmentId(1)
                .departmentName("IT")
                .jobTitle("Software Developer")
                .dateOfJoining(LocalDate.of(2022, 1, 15))
                .birthday(LocalDate.of(1990, 5, 20))
                .role("EMPLOYEE")
                .managerId(2)
                .managerName("Jane Smith")
                .createdAt(LocalDateTime.of(2022, 1, 15, 9, 0))
                .build();

        employeeDto2 = EmployeeDTO.builder()
                .id(2)
                .email("jane.smith@test.com")
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("098-765-4321")
                .departmentId(1)
                .departmentName("IT")
                .jobTitle("Team Lead")
                .dateOfJoining(LocalDate.of(2020, 3, 10))
                .birthday(LocalDate.of(1985, 8, 15))
                .role("HR")
                .managerId(null)
                .managerName(null)
                .createdAt(LocalDateTime.of(2020, 3, 10, 9, 0))
                .build();
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void EmployeeController_GetAll_ReturnsEmployeeList() throws Exception {
        // Arrange
        List<EmployeeDTO> employees = Arrays.asList(employeeDto1, employeeDto2);
        when(employeeService.getAll()).thenReturn(employees);

        // Act & Assert
        mockMvc.perform(get("/employee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@test.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }

    @Test
    @WithMockUser(roles = {"EMPLOYEE"})
    public void EmployeeController_GetAll_ReturnsEmployeeListWithEmployeeRole() throws Exception {
        // Arrange
        List<EmployeeDTO> employees = Arrays.asList(employeeDto1, employeeDto2);
        when(employeeService.getAll()).thenReturn(employees);

        // Act & Assert
        mockMvc.perform(get("/employee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void EmployeeController_GetAll_ReturnsNoContent_WhenNoEmployees() throws Exception {
        // Arrange
        when(employeeService.getAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/employee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void EmployeeController_GetById_ReturnsEmployee() throws Exception {
        // Arrange
        Long employeeId = 1L;
        when(employeeService.getById(employeeId)).thenReturn(employeeDto1);

        // Act & Assert
        mockMvc.perform(get("/employee/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@test.com"))
                .andExpect(jsonPath("$.departmentName").value("IT"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    public void EmployeeController_GetById_ReturnsNoContent_WhenEmployeeNotFound() throws Exception {
        // Arrange
        Long employeeId = 999L;
        when(employeeService.getById(employeeId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/employee/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void EmployeeController_GetAll_ReturnsUnauthorized_WhenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/employee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
