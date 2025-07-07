package group2.intranet.project.controllers;

import group2.intranet.project.domain.dtos.LoginRequest;
import group2.intranet.project.domain.dtos.LoginResponseDto;
import group2.intranet.project.domain.entities.Employee;
import group2.intranet.project.services.CustomUserDetailsService;
import group2.intranet.project.services.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequest loginRequest) {

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        LoginResponseDto response = null;

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            if(!auth.isAuthenticated()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Employee employee = (Employee) userDetailsService.loadUserByUsername(loginRequest.getEmail());

            if(employee == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            String token = jwtService.generateToken(userDetails, Long.valueOf(employee.getId())); //Id'yi de token'a ekledik

            response = new LoginResponseDto(token, employee.getId(), employee.getEmail(), employee.getRole(), employee.getFirstName(), employee.getLastName());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
