package group2.intranet.project.domain.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrgEmployeeDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String departmentName;
    private String email;
    private String phoneNumber;

    @Builder.Default
    private List<OrgEmployeeDTO> reports = new ArrayList<>();
}
