package com.printer.fileque.security;

import com.printer.fileque.entities.User;
import com.printer.fileque.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtUserSyncFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken token) {

                String userId = String.valueOf(token.getTokenAttributes().get("sub"));
                String firstName = String.valueOf(token.getTokenAttributes().get("given_name"));
                String lastName = String.valueOf(token.getTokenAttributes().get("family_name"));
                String email = String.valueOf(token.getTokenAttributes().get("email"));

                User user = new User(email, firstName, lastName, userId);

                userService.syncUser(user);
                logger.debug("User synchronized: " + email);
            } else {
                logger.warn("No JWT token found in security context");
            }
        } catch (Exception e) {
            logger.error("Error during user synchronization", e);
            throw new IllegalArgumentException("Unable to auth user", e);
        }
        filterChain.doFilter(request, response);
    }
}
