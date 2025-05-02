package org.example.clic.security;

import org.example.clic.model.User;
import org.example.clic.service.UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

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
                    return u;
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setGoogleId(googleId);
                    u.setName(name);
                    u.setEmail(email);
                    u.setRole(User.Role.CLIENT);
                    return u;
                });

        userService.save(user);
        return oidcUser;
    }
}
