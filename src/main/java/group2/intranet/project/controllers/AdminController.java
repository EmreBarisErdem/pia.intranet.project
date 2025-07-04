package group2.intranet.project.controllers;

import group2.intranet.project.services.EmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeService employeeService;

    public AdminController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

//    @PostMapping("/migrate-passwords")
//    public ResponseEntity<String> migratePasswords() {
//        employeeService.migratePasswords();
//        return ResponseEntity.ok("Passwords migrated successfully.");
//    }
}
