package dev.app.iCheck.security;

import dev.app.iCheck.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Usuwamy "Bearer " z tokenu

            if (jwtUtil.validateToken(token)) {
                // Jeśli token jest poprawny, wyciągamy informacje o użytkowniku i rolach
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractAllClaims(token).get("role", String.class); // Wyciąganie roli z tokenu

                // Tworzymy autentykację
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

                // Ustawiamy autentykację w kontekście Spring Security
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                // Jeśli token jest nieprawidłowy, zwracamy status HTTP 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return; // Zatrzymujemy przetwarzanie żądania
            }
        }

        filterChain.doFilter(request, response); // Kontynuujemy dalsze przetwarzanie żądania
    }
}