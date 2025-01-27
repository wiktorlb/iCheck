package dev.app.iCheck.config;

import dev.app.iCheck.security.AuthenticationFilter;
import dev.app.iCheck.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationFilter authenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
            AuthenticationFilter authenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationFilter = authenticationFilter;
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //             .csrf().disable()
    //             .cors().and() // Włącz obsługę CORS
    //             .authorizeRequests() // Używamy authorizeRequests do autoryzacji
    //             .requestMatchers("/api/auth/login").permitAll() // Zezwalaj na dostęp do logowania dla wszystkich
    //             .requestMatchers("/api/users").hasRole("ADMIN") // Tylko dla administratorów - zarządzanie użytkownikami
    //             .requestMatchers("/api/users/**/roles/add", "/api/users/**/roles/remove").hasRole("ADMIN") // Dodawanie/Usuwanie
    //                                                                                                        // ról -
    //                                                                                                        // ADMIN
    //             .requestMatchers("/api/auth/register").hasRole("ADMIN") // Rejestracja - tylko dla ADMIN
    //             .requestMatchers("/api/flights").hasRole("LEADER") // Dodawanie lotów - tylko dla LEADER
    //             .requestMatchers("/api/flights/**/passengers/upload").hasRole("LEADER") // Dodawanie pasażerów - tylko
    //                                                                                     // dla LEADER
    //             .requestMatchers("/api/flights/**/delete").hasRole("LEADER") // Usuwanie lotów - tylko dla LEADER
    //             .requestMatchers("/api/flights/**/status").hasRole("USER") // Zmiana statusu lotu - tylko dla USER
    //             .requestMatchers("/management/**", "/flightboard/**").authenticated() // Wymaga autoryzacji
    //             .anyRequest().permitAll() // Pozwól na dostęp do innych publicznych zasobów
    //             .and()
    //             .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // Dodanie filtra

    //     return http.build();
    // }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and() // Włącz obsługę CORS
                .authorizeRequests() // Używamy authorizeRequests do autoryzacji
                .requestMatchers("/api/auth/login").permitAll() // Zezwalaj na dostęp do loginu
                .requestMatchers("/api/flights").hasAnyRole("USER", "LEADER", "ADMIN") // Zezwalaj na dostęp do lotów
                .requestMatchers("/api/auth/userinfo").authenticated() // Endpoint z autentykacją
                .requestMatchers("/flightboard/**").authenticated() // Wymaga autoryzacji
                .requestMatchers("/register", "/add-flight").hasRole("ADMIN") // Dostęp tylko dla administratorów
                .requestMatchers("/management/**").hasRole("ADMIN")
                .requestMatchers("api/users").hasRole("ADMIN") // Tylko admin może zarządzać użytkownikami
                .requestMatchers("/add-passenger").hasAnyRole("LEADER", "ADMIN") // Leader i admin mogą dodawać
                                                                                 // pasażerów
                .requestMatchers("/delete-flight").hasRole("ADMIN") // Tylko admin może usuwać loty
                .requestMatchers("/update-status").hasAnyRole("USER", "LEADER", "ADMIN") // User, Leader, Admin mogą
                                                                                         // zmieniać status lotu
                .anyRequest().permitAll() // Pozwól na dostęp do innych publicznych zasobów
                .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // Dodanie filtra

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Haszowanie haseł
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Zezwól na dostęp z lokalnego frontend
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}