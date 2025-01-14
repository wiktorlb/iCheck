// package dev.app.iCheck.security;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.Claims;
// import org.springframework.stereotype.Component;

// import javax.crypto.SecretKey;
// import java.util.Date;

// @Component
// public class JWTUtil {



//     // Tworzymy bezpieczny klucz o odpowiedniej długości dla algorytmu HMAC-SHA256
//     private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

//     // Generowanie tokenu
//     public String generateToken(String username, String role) {
//         return Jwts.builder()
//             .setHeaderParam("typ", "JWT") // Dodanie typu JWT w nagłówku
//             .setSubject(username)
//             .claim("role", role)
//             .setIssuedAt(new Date())
//             .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 godzina
//             .signWith(key)
//             .compact();
//     }

//     // Walidacja tokenu JWT
//     public boolean validateToken(String token) {
//         try {
//             System.out.println("Validating Token: " + token);
//             Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//             return true;
//         } catch (Exception e) {
//             System.out.println("Token validation failed: " + e.getMessage());
//             return false;
//         }
//     }

//     // Wyciąganie nazwy użytkownika z tokenu
//     public String extractUsername(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getSubject();
//     }

//     // Wyciąganie wszystkich danych z tokenu (np. ról)
//     public Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }
// }

package dev.app.iCheck.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey; // Wczytanie klucza z pliku konfiguracyjnego

    private SecretKey key; // Klucz, który zostanie zainicjowany po wstrzyknięciu

    public JWTUtil() {
        // Zmieniamy inicjalizację klucza, aby zapewnić odpowiednią długość
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Tworzymy bezpieczny klucz dla HS256
    }

    // Generowanie tokenu
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT") // Dodanie typu JWT w nagłówku
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 godzina
                .signWith(key)
                .compact();
    }

    // Walidacja tokenu JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    // Wyciąganie nazwy użytkownika z tokenu
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Wyciąganie wszystkich danych z tokenu (np. ról)
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}