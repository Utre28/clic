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

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    public CustomOidcUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        OidcUser oidcUser = super.loadUser(request);

        String googleId = oidcUser.getSubject();
        String email    = oidcUser.getEmail();
        String name     = oidcUser.getFullName();

        // Crea o actualiza el usuario en BD
        User user = userService.findByGoogleId(googleId)
                .map(u -> {
                    u.setName(name);
                    u.setEmail(email);
                    // Actualiza el rol según email aunque ya exista
                    if ("edwardrozo2010@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.PHOTOGRAPHER);
                    } else if ("sayaline.ik@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.PHOTOGRAPHER);
                    } else if ("wallapopalejandro50@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.PHOTOGRAPHER);
                    } else {
                        u.setRole(User.Role.CLIENT);
                    }
                    return u;
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setGoogleId(googleId);
                    u.setName(name);
                    u.setEmail(email);
                    // Aquí asignamos el rol según email
                    if ("edwardrozo2010@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.PHOTOGRAPHER); // Asumiendo que tienes este enum
                    } else if ("sayaline.ik@gmail.com".equalsIgnoreCase(email)) {
                        u.setRole(User.Role.ADMIN);
                    } else {
                        u.setRole(User.Role.CLIENT);
                    }

                    return u;
                });

        userService.save(user);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new CustomOidcUser(user, Collections.singleton(authority), oidcUser);
    }
}