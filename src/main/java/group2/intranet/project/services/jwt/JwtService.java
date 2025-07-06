package group2.intranet.project.services.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    //// application.properties'den bu değer alınır.
    @Value("${jwt.secret}")
    private String SECRET_KEY;


    /// Bu metod:
    /// sub olarak kullanıcı adını (genellikle email) belirler.
    /// "role" claim'ine kullanıcının ilk yetkisini (ROLE_HR, ROLE_EMPLOYEE, vb.) yazar.
    /// 10 saatlik geçerlilik süresi tanımlar.
    /// HS256 algoritması ile imzalar.
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // username (örneğin email) set edilir
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())  // rol eklenir

                .setIssuedAt(new Date())                                                        // oluşturulma zamanı
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 saat
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /// Token içindeki sub (subject) alanını yani username/email’i döner.
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /// Token’daki username ile UserDetails içindeki username eşleşiyor mu?
    ///
    /// Token süresi dolmamış mı?
    ///
    /// Bu iki kriter sağlanıyorsa, token geçerlidir.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /// Token’ın geçerlilik süresi geçmişse true döner.
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /// JWT’yi parse edip tüm claim’leri döner.
    ///
    /// İmza doğrulaması da burada yapılır (secret key ile).
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /// Genel amaçlı bir yöntemdir. İstenilen claim’i işlevsel olarak döndürmek için kullanılır.
    ///
    /// Örneğin:
    /// String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /// Secret key byte dizisine dönüştürülüp HMAC algoritmasına uygun bir Key nesnesi oluşturulur.
    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
