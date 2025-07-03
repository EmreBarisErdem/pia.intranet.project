package group2.intranet.project.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementDTO {
    private Integer id;
    private String title;
    private String content;
    @JsonProperty("isPublic")
    private boolean publicAnnouncement;
    private LocalDateTime expiresAt;
    private String status;
    private LocalDateTime createdAt;
    private Integer createdById;
    private String createdByName;
}
