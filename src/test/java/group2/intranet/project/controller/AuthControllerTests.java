// package group2.intranet.project.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import group2.intranet.project.controllers.AuthController;
// import group2.intranet.project.domain.dtos.LoginRequest;
// import group2.intranet.project.domain.entities.Employee;
// import group2.intranet.project.services.CustomUserDetailsService;
// import group2.intranet.project.services.jwt.JwtService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(AuthController.class)
// @Import({TestSecurityConfig.class})
// public class AuthControllerTests {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private AuthenticationManager authenticationManager;

//     @MockBean
//     private JwtService jwtService;

//     @MockBean
//     private CustomUserDetailsService userDetailsService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private Employee hrEmployee;
//     private Employee regularEmployee;
//     private LoginRequest validLoginRequest;
//     private LoginRequest invalidLoginRequest;

//     @BeforeEach
//     void setup() {
//         hrEmployee = Employee.builder()
//                 .id(1)
//                 .email("hr@company.com")
//                 .passwordHash("$2a$10$hashedPassword")
//                 .firstName("HR")
//                 .lastName("Manager")
//                 .phoneNumber("123-456-7890")
//                 .jobTitle("HR Manager")
//                 .dateOfJoining(LocalDate.of(2020, 1, 15))
//                 .birthday(LocalDate.of(1985, 5, 10))
//                 .role("ROLE_HR")
//                 .createdAt(LocalDateTime.now())
//                 .build();

//         regularEmployee = Employee.builder()
//                 .id(2)
//                 .email("employee@company.com")
//                 .passwordHash("$2a$10$hashedPassword")
//                 .firstName("John")
//                 .lastName("Doe")
//                 .phoneNumber("123-456-7891")
//                 .jobTitle("Software Developer")
//                 .dateOfJoining(LocalDate.of(2022, 3, 1))
//                 .birthday(LocalDate.of(1990, 8, 15))
//                 .role("ROLE_EMPLOYEE")
//                 .createdAt(LocalDateTime.now())
//                 .build();

//         validLoginRequest = new LoginRequest();
//         validLoginRequest.setEmail("hr@company.com");
//         validLoginRequest.setPassword("password123");

//         invalidLoginRequest = new LoginRequest();
//         invalidLoginRequest.setEmail("invalid@company.com");
//         invalidLoginRequest.setPassword("wrongpassword");
//     }

//     @Test
//     public void AuthController_Login_ValidCredentials_ReturnsJwtToken() throws Exception {
//         // Arrange
//         String expectedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoci5jb21wYW55LmNvbSIsInJvbGUiOiJST0xFX0hSIiwiaWQiOjEsImlhdCI6MTYyMzQ1Njc4OSwiZXhwIjoxNjIzNDkyNzg5fQ.signature";
        
//         Authentication mockAuth = new UsernamePasswordAuthenticationToken(
//                 hrEmployee.getEmail(), 
//                 "password123",
//                 List.of(new SimpleGrantedAuthority("ROLE_HR"))
//         );

//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenReturn(mockAuth);
//         when(userDetailsService.loadUserByUsername("hr@company.com"))
//                 .thenReturn(hrEmployee);
//         when(userDetailsService.getEmployeeByEmail("hr@company.com"))
//                 .thenReturn(hrEmployee);
//         when(jwtService.generateToken(hrEmployee, 1L))
//                 .thenReturn(expectedToken);

//         // Act & Assert
//         mockMvc.perform(post("/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(validLoginRequest)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.token").value(expectedToken));
//     }

//     @Test
//     public void AuthController_Login_EmployeeRole_ReturnsJwtToken() throws Exception {
//         // Arrange
//         String expectedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbXBsb3llZUBjb21wYW55LmNvbSIsInJvbGUiOiJST0xFX0VNUExPWUVFIiwiaWQiOjIsImlhdCI6MTYyMzQ1Njc4OSwiZXhwIjoxNjIzNDkyNzg5fQ.signature";
        
//         LoginRequest employeeLoginRequest = new LoginRequest();
//         employeeLoginRequest.setEmail("employee@company.com");
//         employeeLoginRequest.setPassword("password123");

//         Authentication mockAuth = new UsernamePasswordAuthenticationToken(
//                 regularEmployee.getEmail(), 
//                 "password123",
//                 List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
//         );

//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenReturn(mockAuth);
//         when(userDetailsService.loadUserByUsername("employee@company.com"))
//                 .thenReturn(regularEmployee);
//         when(userDetailsService.getEmployeeByEmail("employee@company.com"))
//                 .thenReturn(regularEmployee);
//         when(jwtService.generateToken(regularEmployee, 2L))
//                 .thenReturn(expectedToken);

//         // Act & Assert
//         mockMvc.perform(post("/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(employeeLoginRequest)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.token").value(expectedToken));
//     }

//     @Test
//     public void AuthController_Login_InvalidCredentials_ReturnsNotFound() throws Exception {
//         // Arrange
//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenThrow(new BadCredentialsException("Invalid credentials"));

//         // Act & Assert
//         mockMvc.perform(post("/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(invalidLoginRequest)))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void AuthController_Login_EmptyEmail_ReturnsNotFound() throws Exception {
//         // Arrange
//         LoginRequest emptyEmailRequest = new LoginRequest();
//         emptyEmailRequest.setEmail("");
//         emptyEmailRequest.setPassword("password123");

//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenThrow(new BadCredentialsException("Invalid credentials"));

//         // Act & Assert
//         mockMvc.perform(post("/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(emptyEmailRequest)))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void AuthController_Login_EmptyPassword_ReturnsNotFound() throws Exception {
//         // Arrange
//         LoginRequest emptyPasswordRequest = new LoginRequest();
//         emptyPasswordRequest.setEmail("hr@company.com");
//         emptyPasswordRequest.setPassword("");

//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenThrow(new BadCredentialsException("Invalid credentials"));

//         // Act & Assert
//         mockMvc.perform(post("/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(emptyPasswordRequest)))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void AuthController_Login_NullRequestBody_ReturnsBadRequest() throws Exception {
//         // Act & Assert
//         mockMvc.perform(post("/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     public void AuthController_Login_MalformedJson_ReturnsBadRequest() throws Exception {
//         // Act & Assert
//         mockMvc.perform(post("/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{\"email\":\"test@company.com\",\"password\":}"))
//                 .andExpect(status().isBadRequest());
//     }
// }
