package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.DepartmentDTO;
import group2.intranet.project.services.DepartmentService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departments")
@Validated
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping()
    public ResponseEntity<List<DepartmentDTO>> getAll() {

        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        System.out.println(departments.toString());
        if (departments.isEmpty()){
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(departments); // 200 OK



    }

    @GetMapping("{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable("id") @Min(value = 1,  message = "ID must be greater than or equal to 1") Integer departmentId){

        DepartmentDTO department = departmentService.getDepartmentById(departmentId);
        System.out.println(department.toString());

        if(department == null){
            return ResponseEntity.noContent().build(); // 404 Not Found
        }
        System.out.println("CHECK GEÇTİ");

        return ResponseEntity.ok(department); //200 OK
    }
}
