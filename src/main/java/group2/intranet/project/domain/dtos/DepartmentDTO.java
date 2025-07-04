package group2.intranet.project.domain.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {

    private Integer id;
    private String name;
    private String location;
    private String email;
    private LocalDateTime createdAt;


    /*
    private List<Integer> employeeIds;
    private List<Integer> eventIds;
    private List<Integer> announcementIds;
    private List<Integer> newsIds;
    private List<Integer> documentIds;
    */

}
