package org.example.clic.security;

import org.example.clic.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          CustomAuthenticationSuccessHandler successHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index",
                                "/css/**", "/js/**", "/img/**",
                                "/login", "/register", "/error",
                                "/verify-code", "/verify-email", "/forgot-password", "/reset-password",
                                "/oauth2/**",
                                "/portafolio", "/portafolio/**",
                                "/eventos", "/contacto", "/subir-fotos", "/eventos/**",
                                "/categoria/**", // Rutas públicas
                                "/albumes/by-event/**",
                                "/albumes", // acceso público a listado general
                                "/album/**", // acceso público a álbumes y fotos
                                "/uploads/**", // acceso público a fotos subidas
                                "/api/events/**", "/api/albums/**", "/api/photos/**", "/albumes/by-event/**",
                                "/api/photos/by-album/**", // acceso público a fotos por álbum
                                "/terminos-condiciones.html" // Ruta añadida para Términos y Condiciones
                        ).permitAll() // Permitir estas rutas sin autenticación
                        .requestMatchers("/panel/**").hasRole("PHOTOGRAPHER") // Solo accesible para fotógrafos
                        .requestMatchers("/perfil/**").authenticated() // Accesible para usuarios autenticados
                        .requestMatchers("/albumes/mis-eventos").hasRole("CLIENT") // <-- CORREGIDO: proteger la ruta correcta
                        .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Página de login personalizada
                        .loginProcessingUrl("/login") // URL para procesar el login
                        .successHandler(successHandler) // Redirigir después de login exitoso
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // Página de login personalizada para OAuth2
                        .successHandler(successHandler) // Redirigir después de login exitoso
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para procesar logout
                        .logoutSuccessUrl("/login?logout") // Redirigir después de logout
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable())  // Deshabilitar CSRF (generalmente usado para APIs)
                .userDetailsService(customUserDetailsService); // Servicio personalizado de detalles de usuario

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/uploads/**"); // Ignorar rutas para archivos estáticos
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Encriptación de contraseñas
    }
}