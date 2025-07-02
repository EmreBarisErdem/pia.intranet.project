package group2.intranet.project.repositories;

import group2.intranet.project.domain.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
