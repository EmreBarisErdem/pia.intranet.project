package group2.intranet.project.services.jwt;

import group2.intranet.project.services.CustomUserDetailsService;
import group2.intranet.project.services.CustomWebAuthenticationDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Log
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    /// Bu yapı sayesinde, Token doğrulandıysa,
    /// Token içindeki "role" bilgisi "ROLE_HR" gibi yetki olarak SecurityContext'e yazıldıysa,
    /// Artık SecurityConfig'teki bu kural işler:
    /// .requestMatchers("/hr/**").hasRole("HR")
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info(">>> JwtAuthFilter çalıştı:" +  request.getRequestURI());

        ///İstek Authorization: Bearer <token> başlığıyla gelmiş mi kontrol eder.
        final String authHeader = request.getHeader("Authorization");

        ///Login olmayanlar için veya JWT taşımayan istekler için diğer filtrelere geçer.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        ///Token'dan Username, Role ve Id bilgilerini çıkar...
        final String token = authHeader.substring(7);
        final String username = jwtService.extractUsername(token);
        final String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        final Long userId = jwtService.extractClaim(token, claims -> claims.get("id", Long.class)); // ← id çekiyoruz

        ///Güvenlik bağlamı boşsa ve kullanıcı geçerliyse → yetkilendirme yapılır ve kullanıcı daha önce authenticate edilmemişse işlem yapılır.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            ///Token geçerliyse, kullanıcı ve rollerle birlikte Authentication objesi oluşturulur
            if (jwtService.isTokenValid(token, userDetails)) {

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                ///userId'yi isteğe özel attribute olarak ekleyelim
                //authToken.setDetails();


                ///Spring Security, sonraki @PreAuthorize, hasRole, hasAuthority gibi kontrollerde bu kimliği kullanır.
                CustomWebAuthenticationDetails customDetails = new CustomWebAuthenticationDetails(request, userId);
                authToken.setDetails(customDetails);
            }
        }
        log.info("Authorization Header: " + request.getHeader("Authorization"));
        log.info("Request method:" + request.getMethod() + "  " + "Content-Type: " + request.getContentType());
        log.info("Token içeriği:" + token);

        filterChain.doFilter(request, response);


    }

}
