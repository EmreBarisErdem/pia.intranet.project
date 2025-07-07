package group2.intranet.project.config;

import group2.intranet.project.services.CustomUserDetailsService;
import group2.intranet.project.services.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                ///JWT kullandığımız için CSRF'yi (Cross-Site Request Forgery) kapattık.
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                    /// login authentication is open for all
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/confessions").permitAll()
                        .requestMatchers("/confessions/submit").permitAll()
                        .requestMatchers("/confessions/delete/**").hasRole("HR")
                    /// employee endpoint authorizations
                        .requestMatchers("/employee/**").hasAnyRole("HR", "EMPLOYEE")
                    /// departments endpoint authorizations
                        .requestMatchers("/departments/**").hasAnyRole("HR", "EMPLOYEE")
                    /// announcements endpoint authorizations
                        .requestMatchers("/announcements").hasAnyRole("HR", "EMPLOYEE")
                        .requestMatchers("/announcements/create/**").hasRole("HR")
                        .requestMatchers("/announcements/update/**").hasRole("HR")
                        .requestMatchers("/announcements/delete/**").hasRole("HR")
                    /// documents endpoint authorizations
                        .requestMatchers("/documents").hasAnyRole("HR", "EMPLOYEE")
                        .requestMatchers("/documents/download/**").hasAnyRole("HR", "EMPLOYEE")
                        .requestMatchers("/documents/delete/**").hasRole("HR")
                        .requestMatchers("/documents/upload").hasRole("HR")
                    /// news endpoint authorizations
                        .requestMatchers("/news").hasAnyRole("HR", "EMPLOYEE")
                        .requestMatchers("/news/create").hasRole("HR")
                        .requestMatchers("/news/update/**").hasRole("HR")
                        .requestMatchers("/news/delete/**").hasRole("HR")
                        .requestMatchers("/news/type/**").hasAnyRole("HR", "EMPLOYEE")
                        .requestMatchers("/news/{id}/image").hasAnyRole("HR", "EMPLOYEE")
                    /// event endpoint authorizations
                        .requestMatchers("/events").hasAnyRole("HR", "EMPLOYEE")
                        .requestMatchers("/events/create").hasAnyRole("HR", "EMPLOYEE")
                        .requestMatchers("/events/update/**").hasRole("HR")
                        .requestMatchers("/events/delete/**").hasRole("HR")
                    /// organizationChart endpoint authorizations
                        .requestMatchers("/chart").hasRole("HR")
                        .requestMatchers("/chart/**").hasAnyRole("HR","EMPLOYEE")
                    .anyRequest().authenticated()
                   ///.requestMatchers("/**").permitAll() //for test
                )
                ///Kimlik doğrulama sonrası kullanıcı bilgilerini getirir.
                .userDetailsService(userDetailsService)
                ///Sunucuda session tutulmaz, Her istek JWT ile kimlik doğrulama yapar
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                ///İstekten önce JWT token'ı kontrol eder ve güvenliği sağlar.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
