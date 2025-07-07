package group2.intranet.project.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Getter
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    private final Long userId;

    public CustomWebAuthenticationDetails(HttpServletRequest request, Long userId) {
        super(request);
        this.userId = userId;
    }

}
