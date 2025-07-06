package group2.intranet.project.domain.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    private Integer id;

    private String title;

    private String description;

    private String eventType;

    private Integer maxParticipants;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private Boolean isApproved;

    private LocalDateTime createdAt;

    private Integer createdById;

    private List<Integer> departmentIds;

}
