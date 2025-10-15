package com.jkmconfecciones.Integrador_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/auth/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().permitAll() // modo pruebas
                )
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")      // Spring maneja este endpoint
                        .logoutSuccessUrl("/")          // Redirige al login (index.html)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
