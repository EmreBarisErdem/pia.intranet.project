package group2.intranet.project.repositories;

import group2.intranet.project.domain.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.manager")
    List<Employee> findAllWithManager();

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.manager WHERE e.id = :id")
    Optional<Employee> findByIdWithManager(@Param("id") Integer id);

    List<Employee> findByManagerId(Integer managerId);
}
