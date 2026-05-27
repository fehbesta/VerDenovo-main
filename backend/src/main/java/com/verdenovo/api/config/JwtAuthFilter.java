package com.verdenovo.api.config;

import com.verdenovo.api.repository.UsuarioRepository;
import com.verdenovo.api.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtil.validateToken(token, username)) {
                        String tipo = jwtUtil.getTipoFromToken(token);
                        List<SimpleGrantedAuthority> authorities = "PONTO".equals(tipo)
                                ? List.of(new SimpleGrantedAuthority("PONTO"))
                                : usuarioRepository.findByEmail(username)
                                    .map(u -> List.of(new SimpleGrantedAuthority(u.getNivelAcesso())))
                                    .orElse(List.of());
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.warn("Token JWT expirado: {}", e.getMessage(), e);
            } catch (SignatureException e) {
                log.warn("Assinatura JWT inválida: {}", e.getMessage(), e);
            } catch (MalformedJwtException e) {
                log.warn("Token JWT malformado: {}", e.getMessage(), e);
            } catch (UnsupportedJwtException e) {
                log.warn("Token JWT não suportado: {}", e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                log.warn("Token JWT vazio ou nulo: {}", e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
