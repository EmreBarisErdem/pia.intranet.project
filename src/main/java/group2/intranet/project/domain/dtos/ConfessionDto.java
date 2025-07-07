package group2.intranet.project.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfessionDto {

    private Integer id;

    private String nickname;

    private String confession;

    private String ageGap;

    private String department;

    private LocalDateTime timeOfConfession = LocalDateTime.now();

    private boolean termsAccepted;
}
