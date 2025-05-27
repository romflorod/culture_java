package com.app.culture.service;

import com.app.culture.model.Anime;
import com.app.culture.model.User;
import com.app.culture.model.UserAnime;
import com.app.culture.repository.UserAnimeRepository;
import com.app.culture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserAnimeRepository userAnimeRepository;
    
    public User registerNewUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Este nombre de usuario ya está en uso");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Este correo electrónico ya está registrado");
        }
        
        // Encriptar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public boolean authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
    
    // Métodos para manejar animes del usuario
    public void markAnimeForUser(Long userId, Long animeId, String status) {
        // Buscar si ya existe una relación entre este usuario y anime
        Optional<UserAnime> existingMark = userAnimeRepository.findByUserIdAndAnimeId(userId, animeId);
        
        if (existingMark.isPresent()) {
            // Actualizar el status
            UserAnime userAnime = existingMark.get();
            userAnime.setStatus(status);
            userAnimeRepository.save(userAnime);
        } else {
            // Crear nueva relación
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Anime anime = new Anime();
            anime.setId(animeId);
            
            UserAnime userAnime = new UserAnime();
            userAnime.setUser(user);
            userAnime.setAnime(anime);
            userAnime.setStatus(status);
            userAnimeRepository.save(userAnime);
        }
    }
    
    public List<UserAnime> getUserAnimes(Long userId) {
        return userAnimeRepository.findByUserId(userId);
    }
    
    public List<UserAnime> getUserAnimesByStatus(Long userId, String status) {
        return userAnimeRepository.findByUserIdAndStatus(userId, status);
    }
}