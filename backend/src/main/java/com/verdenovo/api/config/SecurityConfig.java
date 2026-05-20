package com.verdenovo.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF: stateless JWT API — sem cookies de sessão, portanto não vulnerável ao ataque clássico de CSRF.
        // Mitigado por: CORS restrito a origens confiáveis + JWT no header Authorization (não em cookie).
        // Endpoints sensíveis exigem token JWT válido que o browser não envia automaticamente.
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/cadastro").permitAll()
                .requestMatchers("/api/auth/recuperar-senha", "/api/auth/verificar-codigo", "/api/auth/redefinir-senha").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/pontos", "/api/categorias").permitAll()
                .requestMatchers("/api/pontos/login").permitAll()
                .requestMatchers("/api/auth/usuarios", "/api/auth/usuarios/**").hasAuthority("ADMIN")
                .requestMatchers("/api/pontos/todos", "/api/pontos/pendentes").hasAuthority("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/pontos/*/aprovar").hasAuthority("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/pontos/*/rejeitar").hasAuthority("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/pontos/*").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/pontos").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toList();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
