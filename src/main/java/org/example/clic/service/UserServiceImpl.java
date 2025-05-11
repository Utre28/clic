// src/main/java/org/example/clic/service/UserServiceImpl.java
package org.example.clic.service;

import org.example.clic.model.User;
import org.example.clic.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override public List<User> findAll() { return userRepository.findAll(); }
    @Override public Optional<User> findById(Long id) { return userRepository.findById(id); }
    @Override public User save(User user) { return userRepository.save(user); }
    @Override public void deleteById(Long id) { userRepository.deleteById(id); }
    @Override public boolean existsById(Long id) { return userRepository.existsById(id); }

    // <-- implementamos la búsqueda por Google ID
    @Override
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
}
