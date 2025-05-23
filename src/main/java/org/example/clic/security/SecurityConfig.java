package org.example.clic.security;

import org.example.clic.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // Clase de configuración de Spring
@EnableWebSecurity // Activa la seguridad web de Spring Security
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;
    private final CustomUserDetailsService customUserDetailsService;

    // Inyección de servicios personalizados para OAuth2 y detalles de usuario
    public SecurityConfig(CustomOidcUserService customOidcUserService,
                          CustomUserDetailsService customUserDetailsService) {
        this.customOidcUserService = customOidcUserService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas sin autenticación
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/login.html",
                                "/register.html",
                                "/api/public/**",
                                "/uploads/**",
                                "/error"
                        ).permitAll()
                        // Rutas para fotógrafos y admins
                        .requestMatchers(
                                "/panel",
                                "/api/events/**",
                                "/api/albums/**",
                                "/api/photos/**"
                        ).hasAnyRole("PHOTOGRAPHER","ADMIN")
                        // Resto de API requiere autenticación
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                // Configuración de login vía formulario
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/panel", true)
                        .permitAll()
                )
                // Configuración de OAuth2 con Google
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html")
                        .defaultSuccessUrl("/panel", true)
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
                )
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF para API REST
                .userDetailsService(customUserDetailsService); // Gestiona usuarios y roles

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Cifra contraseñas con BCrypt
    }
}
