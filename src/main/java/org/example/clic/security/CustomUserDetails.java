package org.example.clic.security;

import org.example.clic.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
// Integración de roles CLIENT, PHOTOGRAPHER y ADMIN con Spring Security
public class CustomUserDetails implements UserDetails {

    private final User user;  // Entidad User de la aplicación

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte el enum Role en autoridad ROLE_<ROL>
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // DEVUELVE la contraseña cifrada de la entidad User
    }

    @Override
    public String getUsername() {
        // Se usa el email como nombre de usuario
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
    // Permite acceder a la entidad User completa
    public User getUser() {
        return user;
    }
}

