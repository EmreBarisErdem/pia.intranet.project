package group2.intranet.project.domain.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {

    private Integer id;
    private String title;
    private String description;
    private String fileUrl;
    private String documentType;
    private LocalDateTime uploadedAt;

}
