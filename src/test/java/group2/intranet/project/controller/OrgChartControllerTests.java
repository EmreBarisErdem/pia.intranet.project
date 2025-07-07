package group2.intranet.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group2.intranet.project.domain.dtos.OrgEmployeeDTO;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.OrgChartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(group2.intranet.project.controllers.OrgChartController.class)
@Import(TestSecurityConfig.class)
public class OrgChartControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrgChartService orgChartService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrgEmployeeDTO ceoEmployee;
    private OrgEmployeeDTO managerEmployee;
    private OrgEmployeeDTO regularEmployee;

    @BeforeEach
    void setup() {
        // CEO with subordinates
        ceoEmployee = OrgEmployeeDTO.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .jobTitle("CEO")
                .departmentName("Executive")
                .email("john.doe@company.com")
                .phoneNumber("+1234567890")
                .reports(Arrays.asList(
                        OrgEmployeeDTO.builder()
                                .id(2)
                                .firstName("Jane")
                                .lastName("Smith")
                                .jobTitle("CTO")
                                .departmentName("Technology")
                                .email("jane.smith@company.com")
                                .phoneNumber("+1234567891")
                                .build()
                ))
                .build();

        // Manager with subordinates
        managerEmployee = OrgEmployeeDTO.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .jobTitle("CTO")
                .departmentName("Technology")
                .email("jane.smith@company.com")
                .phoneNumber("+1234567891")
                .reports(Arrays.asList(
                        OrgEmployeeDTO.builder()
                                .id(3)
                                .firstName("Bob")
                                .lastName("Johnson")
                                .jobTitle("Senior Developer")
                                .departmentName("Technology")
                                .email("bob.johnson@company.com")
                                .phoneNumber("+1234567892")
                                .build()
                ))
                .build();

        // Regular employee with no subordinates
        regularEmployee = OrgEmployeeDTO.builder()
                .id(3)
                .firstName("Bob")
                .lastName("Johnson")
                .jobTitle("Senior Developer")
                .departmentName("Technology")
                .email("bob.johnson@company.com")
                .phoneNumber("+1234567892")
                .reports(Collections.emptyList())
                .build();
    }

    private void setupAuthenticationWithUserId(Long userId, String role) {
        Employee employee = new Employee();
        employee.setId(userId.intValue());
        employee.setFirstName("Test");
        employee.setLastName("User");
        employee.setEmail("testuser@example.com");
        employee.setRole(role);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(employee);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void OrgChartController_GetChart_ReturnsOrgTree() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L, "ROLE_EMPLOYEE");
        List<OrgEmployeeDTO> orgTree = Arrays.asList(ceoEmployee);
        when(orgChartService.getOrgTree()).thenReturn(orgTree);

        // Act & Assert
        mockMvc.perform(get("/chart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].jobTitle").value("CEO"))
                .andExpect(jsonPath("$[0].departmentName").value("Executive"))
                .andExpect(jsonPath("$[0].reports.length()").value(1))
                .andExpect(jsonPath("$[0].reports[0].id").value(2))
                .andExpect(jsonPath("$[0].reports[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].reports[0].jobTitle").value("CTO"));

        verify(orgChartService, times(1)).getOrgTree();
    }

    @Test
    public void OrgChartController_GetChart_WithHRRole_ReturnsOrgTree() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L, "ROLE_HR");
        List<OrgEmployeeDTO> orgTree = Arrays.asList(ceoEmployee, managerEmployee);
        when(orgChartService.getOrgTree()).thenReturn(orgTree);

        // Act & Assert
        mockMvc.perform(get("/chart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(orgChartService, times(1)).getOrgTree();
    }

    @Test
    public void OrgChartController_GetChart_EmptyTree_ReturnsEmptyArray() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L, "ROLE_EMPLOYEE");
        when(orgChartService.getOrgTree()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/chart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(orgChartService, times(1)).getOrgTree();
    }

    @Test
    public void OrgChartController_GetChartById_ReturnsSpecificOrgTree() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L, "ROLE_EMPLOYEE");
        Integer employeeId = 2;
        List<OrgEmployeeDTO> specificOrgTree = Arrays.asList(managerEmployee);
        when(orgChartService.getOrgTreeById(employeeId)).thenReturn(specificOrgTree);

        // Act & Assert
        mockMvc.perform(get("/chart/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].jobTitle").value("CTO"))
                .andExpect(jsonPath("$[0].reports.length()").value(1))
                .andExpect(jsonPath("$[0].reports[0].id").value(3));

        verify(orgChartService, times(1)).getOrgTreeById(employeeId);
    }

    @Test
    public void OrgChartController_GetChartById_WithHRRole_ReturnsSpecificOrgTree() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L, "ROLE_HR");
        Integer employeeId = 3;
        List<OrgEmployeeDTO> specificOrgTree = Arrays.asList(regularEmployee);
        when(orgChartService.getOrgTreeById(employeeId)).thenReturn(specificOrgTree);

        // Act & Assert
        mockMvc.perform(get("/chart/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].firstName").value("Bob"))
                .andExpect(jsonPath("$[0].jobTitle").value("Senior Developer"))
                .andExpect(jsonPath("$[0].reports.length()").value(0));

        verify(orgChartService, times(1)).getOrgTreeById(employeeId);
    }

    @Test
    public void OrgChartController_GetChartById_EmptyResult_ReturnsEmptyArray() throws Exception {
        // Arrange
        setupAuthenticationWithUserId(1L, "ROLE_EMPLOYEE");
        Integer employeeId = 999;
        when(orgChartService.getOrgTreeById(employeeId)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/chart/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(orgChartService, times(1)).getOrgTreeById(employeeId);
    }

    @Test
    public void OrgChartController_GetChart_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // Arrange - No authentication setup
        List<OrgEmployeeDTO> orgTree = Arrays.asList(ceoEmployee);
        when(orgChartService.getOrgTree()).thenReturn(orgTree);

        // Act & Assert
        mockMvc.perform(get("/chart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(orgChartService, never()).getOrgTree();
    }

    @Test
    public void OrgChartController_GetChartById_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // Arrange - No authentication setup
        Integer employeeId = 1;
        List<OrgEmployeeDTO> orgTree = Arrays.asList(ceoEmployee);
        when(orgChartService.getOrgTreeById(employeeId)).thenReturn(orgTree);

        // Act & Assert
        mockMvc.perform(get("/chart/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(orgChartService, never()).getOrgTreeById(anyInt());
    }
}
