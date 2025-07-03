package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.EmployeeDTO;
import group2.intranet.project.services.EmployeeService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Validated
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll() {
        List<EmployeeDTO> employees = employeeService.getAll();
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(employees); // 200 OK
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than or equal to 1") Long id) {
        EmployeeDTO employee = employeeService.getById(id);
        if (employee == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(employee); // 200 OK
    }
}
