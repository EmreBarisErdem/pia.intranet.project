package group2.intranet.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Tüm endpoint'ler için
                .allowedOrigins(
                    "http://localhost:8081",  // React dev server
                    "http://localhost:3000",  // Vite default port
                    "http://127.0.0.1:8081", // Alternatif localhost
                    "http://127.0.0.1:3000"  // Alternatif localhost
                )
                .allowedMethods(
                    "GET", 
                    "POST", 
                    "PUT", 
                    "PATCH", 
                    "DELETE", 
                    "OPTIONS",
                    "HEAD"
                )
                .allowedHeaders("*") // Tüm header'lara izin ver
                .allowCredentials(true) // Cookie ve authentication için
                .maxAge(3600); // Pre-flight request cache süresi (1 saat)
    }
}
