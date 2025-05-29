package org.example.clic.service;

import org.example.clic.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
    boolean existsById(Long id);

    // Búsqueda por Google ID (ya existente)
    Optional<User> findByGoogleId(String googleId);

    // Nueva búsqueda por email para el endpoint /me
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
}