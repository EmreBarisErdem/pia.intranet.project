package group2.intranet.project.service;

import group2.intranet.project.domain.dtos.EmployeeDTO;
import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.mappers.EmployeeMapper;
import group2.intranet.project.repositories.EmployeeRepository;
import group2.intranet.project.services.EmployeeServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EmployeeMapper employeeMapper; // Real mapper, not mocked

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setup() {
        // Use the real MapStruct mapper
        employeeMapper = Mappers.getMapper(EmployeeMapper.class);
        // Manually inject dependencies
        employeeService = new EmployeeServiceImpl(employeeRepository, employeeMapper);
        // Note: passwordEncoder is injected via @Autowired in the actual service
    }

    @Test
    public void EmployeeService_GetById_ReturnsEmployeeDTO() {
        // Arrange
        Long employeeId = 1L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);
        LocalDate testLocalDate = dateTime.toLocalDate();

        Department testDepartment = Department.builder()
                .id(1)
                .name("IT Department")
                .location("Building A")
                .email("it@company.com")
                .build();

        Employee employee = Employee.builder()
                .id(1)
                .email("test@test.com")
                .passwordHash("hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+905350000000")
                .department(testDepartment)
                .jobTitle("Software Developer")
                .dateOfJoining(testLocalDate)
                .birthday(testLocalDate)
                .role("EMPLOYEE")
                .createdAt(dateTime)
                .build();

        // Mocks
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO result = employeeService.getById(employeeId);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getFirstName()).isEqualTo("John");
        Assertions.assertThat(result.getLastName()).isEqualTo("Doe");
        Assertions.assertThat(result.getEmail()).isEqualTo("test@test.com");
        Assertions.assertThat(result.getJobTitle()).isEqualTo("Software Developer");
        Assertions.assertThat(result.getDepartmentName()).isEqualTo("IT Department");
        Assertions.assertThat(result.getRole()).isEqualTo("EMPLOYEE");
    }

    @Test
    public void EmployeeService_GetById_ThrowsExceptionWhenNotFound() {
        // Arrange
        Long employeeId = 999L;

        // Mocks
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> employeeService.getById(employeeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee not found: " + employeeId);
    }

    @Test
    public void EmployeeService_GetAll_ReturnsEmployeeDTOList() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2025-04-08 12:30", formatter);

        Department testDepartment = Department.builder()
                .id(1)
                .name("IT Department")
                .location("Building A")
                .email("it@company.com")
                .build();

        Employee employee1 = Employee.builder()
                .id(1)
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .department(testDepartment)
                .jobTitle("Developer")
                .role("EMPLOYEE")
                .createdAt(dateTime)
                .build();

        Employee employee2 = Employee.builder()
                .id(2)
                .email("jane@test.com")
                .firstName("Jane")
                .lastName("Smith")
                .department(testDepartment)
                .jobTitle("Designer")
                .role("EMPLOYEE")
                .createdAt(dateTime)
                .build();

        List<Employee> employees = List.of(employee1, employee2);

        // Mocks
        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<EmployeeDTO> result = employeeService.getAll();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getFirstName()).isEqualTo("John");
        Assertions.assertThat(result.get(0).getJobTitle()).isEqualTo("Developer");
        Assertions.assertThat(result.get(1).getFirstName()).isEqualTo("Jane");
        Assertions.assertThat(result.get(1).getJobTitle()).isEqualTo("Designer");
    }

    // @Test
    // public void EmployeeService_MigratePasswords_CallsRepositoryOperations() {
    //     // Arrange
    //     Employee employee1 = Employee.builder()
    //             .id(1)
    //             .email("john@test.com")
    //             .passwordHash("plainPassword1")
    //             .firstName("John")
    //             .lastName("Doe")
    //             .build();

    //     Employee employee2 = Employee.builder()
    //             .id(2)
    //             .email("jane@test.com")
    //             .passwordHash("plainPassword2")
    //             .firstName("Jane")
    //             .lastName("Smith")
    //             .build();

    //     List<Employee> employees = List.of(employee1, employee2);

    //     // Mocks
    //     when(employeeRepository.findAll()).thenReturn(employees);

    //     // Act
    //     employeeService.migratePasswords();

    //     // Assert
    //     verify(employeeRepository, times(1)).findAll();
    //     verify(employeeRepository, times(2)).save(org.mockito.Mockito.any(Employee.class));
    // }
}
