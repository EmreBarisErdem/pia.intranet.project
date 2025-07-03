package group2.intranet.project.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {

    private Integer id;
    private String title;
    private String description;
    private String documentType;
    private LocalDateTime uploadedAt;
    private Integer uploadedById;
    private List<Integer> departmentIds;

    @JsonIgnore
    private MultipartFile file; // for upload
    private byte[] fileData;    // for download
}
