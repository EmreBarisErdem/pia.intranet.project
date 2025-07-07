package group2.intranet.project.controller;

import group2.intranet.project.services.jwt.JwtService;
import group2.intranet.project.services.CustomUserDetailsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class TestSecurityConfig {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/documents").hasAnyRole("HR", "EMPLOYEE")
                .requestMatchers("/documents/download/**").hasAnyRole("HR", "EMPLOYEE")
                .requestMatchers("/documents/delete/**").hasRole("HR")
                .requestMatchers("/documents/upload").hasRole("HR")
                .requestMatchers("/events").hasAnyRole("HR", "EMPLOYEE")
                .requestMatchers("/events/create").hasAnyRole("HR", "EMPLOYEE")
                .requestMatchers("/events/update/**").hasRole("HR")
                .requestMatchers("/events/delete/**").hasRole("HR")
                .requestMatchers("/announcements").hasAnyRole("HR", "EMPLOYEE")
                .requestMatchers("/announcements/create/**").hasRole("HR")
                .requestMatchers("/announcements/update/**").hasRole("HR")
                .requestMatchers("/announcements/delete/**").hasRole("HR")
                .anyRequest().authenticated())
            .build();
    }
}
