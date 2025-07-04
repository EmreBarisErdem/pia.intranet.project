package group2.intranet.project.domain.dtos;

import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeDTO {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private Integer departmentId;
    private String departmentName;

    private String jobTitle;
    private LocalDate dateOfJoining;
    private LocalDate birthday;
    private String role;

    private Integer managerId;
    private String managerName;

    private LocalDateTime createdAt;
}

