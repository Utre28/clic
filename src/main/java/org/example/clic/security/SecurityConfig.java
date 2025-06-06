// src/main/java/org/example/clic/security/SecurityConfig.java
package org.example.clic.security;

import org.example.clic.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                                "/oauth2/**",
                                "/portafolio", "/portafolio/**",
                                "/eventos", "/contacto", "/subir-fotos","/eventos/**","/mis-eventos",
                                "/categoria/**", // Páginas de categoría accesibles para todos
                                "/albumes", "/albumes/**",
                                "/api/events/**", "/api/albums/**", "/api/photos/**" ,"/albumes/by-event/**"// Rutas públicas de API
                        ).permitAll() // Permitir todas estas rutas sin autenticación
                        .requestMatchers("/panel/**").hasRole("PHOTOGRAPHER") // Panel solo para fotógrafos
                        .requestMatchers("/perfil/**").authenticated() // Perfil solo para usuarios autenticados
                        .requestMatchers("/mis-eventos").hasRole("CLIENT")// esta ruta solo sea accesible para usuarios autenticados
                        .anyRequest().authenticated() // Todas las demás rutas requieren autenticación


                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    // Ignora completamente las peticiones a /uploads/** para servir archivos estáticos sin filtro
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/uploads/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
