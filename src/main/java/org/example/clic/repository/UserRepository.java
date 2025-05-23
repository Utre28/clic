package org.example.clic.repository;

import org.example.clic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// Repositorio JPA para operaciones CRUD de User
public interface UserRepository extends JpaRepository<User, Long> {
    // Encuentra un usuario por su ID de Google (OAuth)
    Optional<User> findByGoogleId(String googleId);
    // Encuentra un usuario por correo electrónico
    Optional<User> findByEmail(String email);

}
