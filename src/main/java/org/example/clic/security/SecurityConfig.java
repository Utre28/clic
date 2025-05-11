package org.example.clic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;

    public SecurityConfig(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Permitir acceso libre a los recursos estáticos y tu login.html
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/login.html").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                // 2. Configurar OAuth2 con tu loginPage y successUrl
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html")                                  // tu página custom
                        .defaultSuccessUrl("/panel.html", true)                    // ir siempre al panel
                        .userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(customOidcUserService)
                        )
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

}
