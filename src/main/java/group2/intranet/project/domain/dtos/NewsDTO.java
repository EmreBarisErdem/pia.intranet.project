package group2.intranet.project.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {


    private Integer id;

    private String title;

    private String content;

    private byte[] cover_image; // for download

    //private MultipartFile file; // for upload

    private String newsType;

    private LocalDateTime createdAt;

    private Integer createdById;

    @JsonIgnore
    private List<Integer> departmentIds;

}
