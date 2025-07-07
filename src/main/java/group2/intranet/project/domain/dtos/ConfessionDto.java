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

    private Long id;

    private String nickname;

    private String confession;

    private LocalDateTime timeOfConfession = LocalDateTime.now();

    private boolean termsAccepted;
}
