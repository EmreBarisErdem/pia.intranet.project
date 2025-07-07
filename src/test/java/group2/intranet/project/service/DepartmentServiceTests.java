package group2.intranet.project.service;

import group2.intranet.project.domain.dtos.DepartmentDTO;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.mappers.DepartmentMapper;
import group2.intranet.project.repositories.DepartmentRepository;
import group2.intranet.project.services.DepartmentServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTests {

    @Mock
    private DepartmentRepository departmentRepository;

    private DepartmentMapper departmentMapper; // Real mapper, not mocked

    private DepartmentServiceImpl departmentService;

    @BeforeEach
    void setup() {
        // Use the real MapStruct mapper
        departmentMapper = Mappers.getMapper(DepartmentMapper.class);
        // Manually inject dependencies
        departmentService = new DepartmentServiceImpl(departmentRepository, departmentMapper);
    }

    @Test
    public void DepartmentService_GetAllDepartments_ReturnsDepartmentDTOList() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Department dept1 = Department.builder()
                .id(1)
                .name("IT Department")
                .location("Building A")
                .email("it@company.com")
                .createdAt(dateTime)
                .build();

        Department dept2 = Department.builder()
                .id(2)
                .name("HR Department")
                .location("Building B")
                .email("hr@company.com")
                .createdAt(dateTime)
                .build();

        List<Department> departments = List.of(dept1, dept2);

        // Mocks
        when(departmentRepository.findAll()).thenReturn(departments);

        // Act
        List<DepartmentDTO> result = departmentService.getAllDepartments();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getName()).isEqualTo("IT Department");
        Assertions.assertThat(result.get(0).getLocation()).isEqualTo("Building A");
        Assertions.assertThat(result.get(0).getEmail()).isEqualTo("it@company.com");
        Assertions.assertThat(result.get(1).getName()).isEqualTo("HR Department");
    }

    @Test
    public void DepartmentService_GetDepartmentById_ReturnsDepartmentDTO() {
        // Arrange
        Integer departmentId = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Department department = Department.builder()
                .id(departmentId)
                .name("IT Department")
                .location("Building A")
                .email("it@company.com")
                .createdAt(dateTime)
                .build();

        // Mocks
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // Act
        DepartmentDTO result = departmentService.getDepartmentById(departmentId);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("IT Department");
        Assertions.assertThat(result.getLocation()).isEqualTo("Building A");
        Assertions.assertThat(result.getEmail()).isEqualTo("it@company.com");
        Assertions.assertThat(result.getId()).isEqualTo(departmentId);
    }

    @Test
    public void DepartmentService_GetDepartmentById_ReturnsNullWhenNotFound() {
        // Arrange
        Integer departmentId = 999;

        // Mocks
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act
        DepartmentDTO result = departmentService.getDepartmentById(departmentId);

        // Assert
        Assertions.assertThat(result).isNull();
    }
}
