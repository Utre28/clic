package org.example.clic.security;

import org.example.clic.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
// Extiende DefaultOidcUser para asociar la entidad User a la información OIDC
public class CustomOidcUser extends DefaultOidcUser {

    private final User user;  // Entidad de aplicación vinculada al usuario autenticado

    /**
     * Constructor que recibe la entidad User, las autoridades y el OidcUser delegado.
     */
    public CustomOidcUser(User user, Collection<? extends GrantedAuthority> authorities, OidcUser delegate) {
        super(authorities, delegate.getIdToken(), delegate.getUserInfo());
        this.user = user;
    }
    /**
     * Devuelve la entidad User asociada al usuario OIDC.
     */
    public User getUser() {
        return user;
    }
}
