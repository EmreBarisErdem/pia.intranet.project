package group2.intranet.project.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {

    private Integer id;

    private String title;

    private String content;

    private Integer createdBy;

    @JsonIgnore
    private String coverImage;

    private String newsType;

    private LocalDateTime createdAt;

}
