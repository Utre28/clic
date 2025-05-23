package org.example.clic.service;

import org.example.clic.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga detalles de usuario para Spring Security.
 * Busca la entidad User por email y devuelve un objeto UserDetails.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService; // Servicio para acceder a datos de usuarios

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Carga un usuario por su nombre de usuario (email).
     * @param email Email del usuario
     * @return UserDetails con email, contraseña y roles
     * @throws UsernameNotFoundException si no encuentra el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca la entidad User en la base de datos
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        // Construye y devuelve el objeto UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // Nombre de usuario para autenticación
                .password(user.getPassword())  // Contraseña cifrada
                .authorities("ROLE_" + user.getRole().name()) // Roles con prefijo ROLE_
                .build();
    }
}
