package group2.intranet.project.repository;

import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.domain.entities.Employee;
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
public class EmployeeRepositoryTests {
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private Department testDepartment;
    private Employee testEmployee;

    String str = "2025-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    LocalDate testLocalDate = LocalDate.parse("2025-04-08 12:30", formatter);


    @Autowired
    public EmployeeRepositoryTests(EmployeeRepository employeeRepository,  DepartmentRepository departmentRepository) {
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
////        Not now but can be used to test without writing it all over again.
//        testEmployee = employeeRepository.save(
//                Employee.builder()
//                        .email("test@test.com")
//                        .passwordHash("test")
//                        .firstName("test")
//                        .lastName("test")
//                        .phoneNumber("+905350000000")
//                        .department(testDepartment)
//                        .jobTitle("Test Job Title")
//                        .dateOfJoining(testLocalDate)
//                        .birthday(testLocalDate)
//                        .role("EMPLOYEE")
//                        .createdAt(dateTime)
//                        .build()
//        );
    }


    private Employee createAndSaveTestEmployee(Integer number) {
        Employee employee = Employee.builder()
                        .email("test" + number + "@test.com")
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
                        .build();
        return employeeRepository.save(employee);
    }

    @Test
    public void EmployeeRepository_SaveAll_ReturnsSavedEmployee() {
//        Employee employee = Employee.builder()
//                .email("test@test.com")
//                .passwordHash("test")
//                .firstName("test")
//                .lastName("test")
//                .phoneNumber("+905350000000")
//                .department(testDepartment)
//                .jobTitle("Test Job Title")
//                .dateOfJoining(testLocalDate)
//                .birthday(testLocalDate)
//                .role("EMPLOYEE")
//                .createdAt(dateTime)
//                .build();

//        Employee savedEmployee = employeeRepository.save(employee);


        Employee savedTestEmployee = createAndSaveTestEmployee(1);


        Assertions.assertThat(savedTestEmployee).isNotNull();
        Assertions.assertThat(savedTestEmployee.getId()).isGreaterThan(0);
    }

    @Test
    public void EmployeeRepostory_GetAll_ReturnsMoreThenOneEmployee() {
//        Employee employee = Employee.builder()
//                .email("test@test.com")
//                .passwordHash("test")
//                .firstName("test")
//                .lastName("test")
//                .phoneNumber("+905350000000")
//                .department(testDepartment)
//                .jobTitle("Test Job Title")
//                .dateOfJoining(testLocalDate)
//                .birthday(testLocalDate)
//                .role("EMPLOYEE")
//                .createdAt(dateTime)
//                .build();
//
//        Employee employee2 = Employee.builder()
//                .email("test2@test.com")
//                .passwordHash("test")
//                .firstName("test2")
//                .lastName("test2")
//                .phoneNumber("+905352222222")
//                .department(testDepartment)
//                .jobTitle("Test Job Title")
//                .dateOfJoining(testLocalDate)
//                .birthday(testLocalDate)
//                .role("HR")
//                .createdAt(dateTime)
//                .build();

//        employeeRepository.save(savedTestEmployee);
//        employeeRepository.save(savedTestEmployee2);


        Employee savedTestEmployee = createAndSaveTestEmployee(1);
        Employee savedTestEmployee2 = createAndSaveTestEmployee(2);

        List<Employee> employeeList = employeeRepository.findAll();

        Assertions.assertThat(employeeList).isNotNull();
        Assertions.assertThat(employeeList.size()).isEqualTo(2);
    }

    @Test
    public void EmployeeRepository_FindById_ReturnsSavedEmployee() {
//        Employee employee = Employee.builder()
//                .email("test@test.com")
//                .passwordHash("test")
//                .firstName("test")
//                .lastName("test")
//                .phoneNumber("+905350000000")
//                .department(testDepartment)
//                .jobTitle("Test Job Title")
//                .dateOfJoining(testLocalDate)
//                .birthday(testLocalDate)
//                .role("EMPLOYEE")
//                .createdAt(dateTime)
//                .build();
//
//        employeeRepository.save(employee);


        Employee savedTestEmployee = createAndSaveTestEmployee(1);

        Employee employeeReturn = employeeRepository.findById(Long.valueOf(savedTestEmployee.getId())).get();

        Assertions.assertThat(employeeReturn).isNotNull();
    }

    @Test
    public void EmployeeRepository_UpdateEmployee_ReturnEmployee() {
//        Employee employee = Employee.builder()
//                .email("test@test.com")
//                .passwordHash("test")
//                .firstName("test")
//                .lastName("test")
//                .phoneNumber("+905350000000")
//                .department(testDepartment)
//                .jobTitle("Test Job Title")
//                .dateOfJoining(testLocalDate)
//                .birthday(testLocalDate)
//                .role("EMPLOYEE")
//                .createdAt(dateTime)
//                .build();
//
//        employeeRepository.save(employee);

        Employee savedTestEmployee = createAndSaveTestEmployee(1);

        Employee employeeSave = employeeRepository.findById(Long.valueOf(savedTestEmployee.getId())).get();

        employeeSave.setFirstName("test name");
        employeeSave.setLastName("test surname");
        employeeSave.setPhoneNumber("+905350009999");
        employeeSave.setDepartment(testDepartment);
        employeeSave.setJobTitle("test job title too");
        employeeSave.setBirthday(testLocalDate);
        employeeSave.setRole("HR");

        Employee updatedEmployee = employeeRepository.save(employeeSave);

        Assertions.assertThat(updatedEmployee.getFirstName()).isNotNull();
        Assertions.assertThat(updatedEmployee.getLastName()).isNotNull();
        Assertions.assertThat(updatedEmployee.getPhoneNumber()).isNotNull();
        Assertions.assertThat(updatedEmployee.getDepartment()).isNotNull();
        Assertions.assertThat(updatedEmployee.getJobTitle()).isNotNull();
        Assertions.assertThat(updatedEmployee.getBirthday()).isNotNull();
        Assertions.assertThat(updatedEmployee.getRole()).isNotNull();
    }

    @Test
    public void EmployeeRepository_EmployeeDelete_ReturnEmployeeIsEmpty() {
//        Employee employee = Employee.builder()
//                .email("test@test.com")
//                .passwordHash("test")
//                .firstName("test")
//                .lastName("test")
//                .phoneNumber("+905350000000")
//                .department(testDepartment)
//                .jobTitle("Test Job Title")
//                .dateOfJoining(testLocalDate)
//                .birthday(testLocalDate)
//                .role("EMPLOYEE")
//                .createdAt(dateTime)
//                .build();
//
//        employeeRepository.save(employee);

        Employee savedTestEmployee = createAndSaveTestEmployee(1);

        employeeRepository.deleteById(Long.valueOf(savedTestEmployee.getId()));
        Optional<Employee> employeeReturn = employeeRepository.findById(Long.valueOf(savedTestEmployee.getId()));

        Assertions.assertThat(employeeReturn).isEmpty();
    }

}
