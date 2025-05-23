package org.example.clic.security;

import org.example.clic.model.User;
import org.example.clic.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio personalizado para manejar usuarios OIDC de Google.
 * Crea o actualiza la entidad User según los datos del OidcUser.
 */
@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    public CustomOidcUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Carga o crea el usuario cuando se autentica vía Google.
     */
    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        // Carga información básica del usuario OIDC
        OidcUser oidcUser = super.loadUser(request);

        String googleId = oidcUser.getSubject(); // ID único de Google
        String email    = oidcUser.getEmail(); // Correo del usuario
        String name     = oidcUser.getFullName(); // Nombre completo

        // Crea o actualiza el usuario en BD
        User user = userService.findByGoogleId(googleId)
                .map(u -> {
                    // Actualiza datos existentes
                    u.setName(name);
                    u.setEmail(email);
                    // Actualiza el rol según email aunque ya exista
                    if ("edwardrozo2010@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.PHOTOGRAPHER);
                    } else if ("sayaline.ik@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.ADMIN);

                    } else {
                        u.setRole(User.Role.CLIENT);
                    }
                    return u;
                })
                .orElseGet(() -> {
                    // Crea nuevo usuario si no existe
                    User u = new User();
                    u.setGoogleId(googleId);
                    u.setName(name);
                    u.setEmail(email);
                    // Aquí asignamos el rol según email
                    if ("edwardrozo2010@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.PHOTOGRAPHER); // Asumiendo que tienes este enum
                    } else if ("sayaline.ik@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.ADMIN);
                    } else if ("lejandro50@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.ADMIN);
                    } else {
                        u.setRole(User.Role.CLIENT);
                    }

                    return u;
                });

        userService.save(user); // Guarda cambios o nuevo registro
        // Asigna autoridad basada en el rol
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        // Devuelve CustomOidcUser ligando OIDC y la entidad User
        return new CustomOidcUser(user, Collections.singleton(authority), oidcUser);
    }
}