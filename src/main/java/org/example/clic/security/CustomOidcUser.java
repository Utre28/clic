package org.example.clic.security;

import org.example.clic.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;

public class CustomOidcUser extends DefaultOidcUser {

    private final User user;

    public CustomOidcUser(User user, Collection<? extends GrantedAuthority> authorities, OidcUser delegate) {
        super(authorities, delegate.getIdToken(), delegate.getUserInfo());
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
