// src/main/java/org/example/clic/service/UserService.java
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

    // <-- Aquí añade este método:
    Optional<User> findByGoogleId(String googleId);
}
