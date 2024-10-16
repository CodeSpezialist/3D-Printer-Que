package com.printer.fileque.security;

import com.printer.fileque.exceptions.AccessTokenNotValidException;
import com.printer.fileque.services.AccessTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomTokenValidationFilter extends OncePerRequestFilter {

    private final AccessTokenService accessTokenService;

    public CustomTokenValidationFilter(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request); // Token aus dem Header oder der Anfrage extrahieren

        if (token == null || !accessTokenService.checkIfTokenIsValid(token)) {
            // Wenn kein Token vorhanden oder der Token ungültig ist
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
//            response.getWriter().write("Invalid or missing token");   // Optional: Fehlernachricht an den Client
            throw new AccessTokenNotValidException();
        }

        // Token ist gültig, Authentifizierung setzen
        Authentication authentication = createAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Anfrage weiterleiten
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // Extrahiere den Token aus dem Authorization Header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);  // "Bearer " entfernen und den Token zurückgeben
        }
        return null;
    }

    private Authentication createAuthentication(String token) {
        // Beispiel: Erstelle ein Authentication-Objekt mit dem Token als Principal und einer Rolle
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));  // Beispiel: Standardrolle "ROLE_USER"
        return new UsernamePasswordAuthenticationToken(token, null, authorities);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Token-Überprüfung nur für Pfade, die mit "/api/que" starten
        return !path.startsWith("/api/que");
    }

}
