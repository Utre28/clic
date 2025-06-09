package org.example.clic.service.impl;

import org.example.clic.model.User;
import org.example.clic.dto.UserDTO;
import org.example.clic.service.UserService;
import org.example.clic.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(User.Role.CLIENT); // O el rol que corresponda
        // user.setEnabled(true); // Si tienes este campo
        return userRepository.save(user);
    }

    @Override
    public String getPhotographerRole() {
        return "ROLE_PHOTOGRAPHER";
    }

    @Override
    public String getClientRole() {
        return "ROLE_CLIENT";
    }

    @Override
    public String getRoleByUsername(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && user.getRole() != null) {
            return user.getRole().toString();
        }
        return "";
    }
}
