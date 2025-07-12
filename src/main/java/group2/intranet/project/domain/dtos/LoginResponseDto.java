package group2.intranet.project.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseDto {

    private String token;
    private Integer userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;


}

