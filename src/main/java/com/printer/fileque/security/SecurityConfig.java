package com.printer.fileque.security;

import com.printer.fileque.services.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final AccessTokenService accessTokenService;

    @Autowired
    public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint, AccessTokenService accessTokenService) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.accessTokenService = accessTokenService;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/token/generate").permitAll()
                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api/docs/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new CustomTokenValidationFilter(accessTokenService), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class) // Der CustomTokenValidationFilter wird hier hinzugefügt
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }
}