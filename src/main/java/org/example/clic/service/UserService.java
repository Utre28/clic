package org.example.clic.service;

import org.example.clic.model.User;
import org.example.clic.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
    boolean existsById(Long id);
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
    User registerUser(UserDTO userDTO);
    String getPhotographerRole();
    String getClientRole();
    String getRoleByUsername(String email);
    String encodePassword(String rawPassword);
}