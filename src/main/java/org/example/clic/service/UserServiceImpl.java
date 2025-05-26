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
    private final UserRepository userRepository;// Repositorio para acceso a datos de User

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll(); // Devuelve todos los usuarios
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);  // Busca usuario por ID
    }

    @Override
    public User save(User user) {
        return userRepository.save(user); // Inserta o actualiza usuario
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id); // Elimina usuario por ID
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id); // Verifica existencia
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);  // Busca usuario por Google ID
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);  // Busca usuario por email
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}
