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
                        .requestMatchers("/", "/index", "/login", "/bienvenida", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().permitAll() // permite todo por ahora
                )
                .csrf(csrf -> csrf.disable()); // deshabilitamos CSRF para pruebas

        return http.build();
    }
}


