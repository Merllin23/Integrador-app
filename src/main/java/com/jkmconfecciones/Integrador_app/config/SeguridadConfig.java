package com.jkmconfecciones.Integrador_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SeguridadConfig {

    @Autowired
    private ManejadorFalloAutenticacion manejadorFallo;

    @Autowired
    private ManejadorExitoAutenticacion manejadorExito;

    @Autowired
    private RedireccionSiAutenticadoFilter redireccionSiAutenticadoFilter;

    @Autowired
    private NoCacheFilter noCacheFilter;

    @Bean
    public PasswordEncoder codificadorContrasena() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager administradorAutenticacion(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain cadenaSeguridad(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/registro", "/recuperar", "/restablecer",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/usuario/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/procesarLogin")
                        .successHandler(manejadorExito)
                        .failureHandler(manejadorFallo)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID") // elimina cookie de sesión
                        .clearAuthentication(true)
                        .permitAll()
                )
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable())
                        .frameOptions(frame -> frame.sameOrigin())
                        .httpStrictTransportSecurity(hsts -> hsts.disable())
                )

                // Filtros personalizados
                .addFilterBefore(noCacheFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(redireccionSiAutenticadoFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
