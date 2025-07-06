package group2.intranet.project.repository;

import group2.intranet.project.domain.entities.Department;
import group2.intranet.project.repositories.DepartmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DepartmentRepositoryTests {
    private DepartmentRepository departmentRepository;

    String str = "2025-04-08 12:30";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    @Autowired
    public DepartmentRepositoryTests(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    private Department createAndSaveTestDepartment(Integer number) {
        Department department = Department.builder()
                .name("Test Department " + number)
                .location("Test Location " + number)
                .email("test" + number + "@department.com")
                .build();
        return departmentRepository.save(department);
    }

    @Test
    public void DepartmentRepository_SaveAll_ReturnsSavedDepartment() {
        Department savedTestDepartment = createAndSaveTestDepartment(1);

        Assertions.assertThat(savedTestDepartment).isNotNull();
        Assertions.assertThat(savedTestDepartment.getId()).isGreaterThan(0);
    }

    @Test
    public void DepartmentRepository_GetAll_ReturnsMoreThenOneDepartment() {
        createAndSaveTestDepartment(1);
        createAndSaveTestDepartment(2);

        List<Department> departmentList = departmentRepository.findAll();

        Assertions.assertThat(departmentList).isNotNull();
        Assertions.assertThat(departmentList.size()).isEqualTo(2);
    }

    @Test
    public void DepartmentRepository_FindById_ReturnsSavedDepartment() {
        Department savedTestDepartment = createAndSaveTestDepartment(1);

        Department departmentReturn = departmentRepository.findById(savedTestDepartment.getId()).get();

        Assertions.assertThat(departmentReturn).isNotNull();
    }

    @Test
    public void DepartmentRepository_UpdateDepartment_ReturnDepartment() {
        Department savedTestDepartment = createAndSaveTestDepartment(1);

        Department departmentSave = departmentRepository.findById(savedTestDepartment.getId()).get();

        departmentSave.setName("Updated Department Name");
        departmentSave.setLocation("Updated Location");
        departmentSave.setEmail("updated@department.com");

        Department updatedDepartment = departmentRepository.save(departmentSave);

        Assertions.assertThat(updatedDepartment.getName()).isNotNull();
        Assertions.assertThat(updatedDepartment.getLocation()).isNotNull();
        Assertions.assertThat(updatedDepartment.getEmail()).isNotNull();
        Assertions.assertThat(updatedDepartment.getName()).isEqualTo("Updated Department Name");
    }

    @Test
    public void DepartmentRepository_DepartmentDelete_ReturnDepartmentIsEmpty() {
        Department savedTestDepartment = createAndSaveTestDepartment(1);

        departmentRepository.deleteById(savedTestDepartment.getId());
        Optional<Department> departmentReturn = departmentRepository.findById(savedTestDepartment.getId());

        Assertions.assertThat(departmentReturn).isEmpty();
    }
}
