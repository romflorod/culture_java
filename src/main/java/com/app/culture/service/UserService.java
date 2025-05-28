package com.app.culture.service;

import com.app.culture.model.Anime;
import com.app.culture.model.Book;
import com.app.culture.model.Movie;
import com.app.culture.model.User;
import com.app.culture.model.UserAnime;
import com.app.culture.model.UserBook;
import com.app.culture.model.UserMovie;
import com.app.culture.repository.UserAnimeRepository;
import com.app.culture.repository.UserBookRepository;
import com.app.culture.repository.UserMovieRepository;
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
    private UserMovieRepository userMovieRepository;

    @Autowired
    private UserBookRepository userBookRepository;

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

    public void unmarkAnimeForUser(Long userId, Long animeId) {
        Optional<UserAnime> userAnime = userAnimeRepository.findByUserIdAndAnimeId(userId, animeId);
        
        if (userAnime.isPresent()) {
            userAnimeRepository.delete(userAnime.get());
        } else {
            throw new RuntimeException("No se encontró registro para desmarcar");
        }
    }

    // Métodos para manejar películas del usuario
    public void markMovieForUser(Long userId, Long movieId, String status) {
        // Buscar si ya existe una relación entre este usuario y película
        Optional<UserMovie> existingMark = userMovieRepository.findByUserIdAndMovieId(userId, movieId);
        
        if (existingMark.isPresent()) {
            // Actualizar el status
            UserMovie userMovie = existingMark.get();
            userMovie.setStatus(status);
            userMovieRepository.save(userMovie);
        } else {
            // Crear nueva relación
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Movie movie = new Movie();
            movie.setId(movieId);
            
            UserMovie userMovie = new UserMovie();
            userMovie.setUser(user);
            userMovie.setMovie(movie);
            userMovie.setStatus(status);
            userMovieRepository.save(userMovie);
        }
    }

    public List<UserMovie> getUserMovies(Long userId) {
        return userMovieRepository.findByUserId(userId);
    }

    public List<UserMovie> getUserMoviesByStatus(Long userId, String status) {
        return userMovieRepository.findByUserIdAndStatus(userId, status);
    }
     public void markBookForUser(Long userId, Long bookId, String status) {
        // Buscar si ya existe una relación entre este usuario y libro
        Optional<UserBook> existingMark = userBookRepository.findByUserIdAndBookId(userId, bookId);
        
        if (existingMark.isPresent()) {
            // Actualizar el status
            UserBook userBook = existingMark.get();
            userBook.setStatus(status);
            userBookRepository.save(userBook);
        } else {
            // Crear nueva relación
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Book book = new Book();
            book.setId(bookId);
            
            UserBook userBook = new UserBook();
            userBook.setUser(user);
            userBook.setBook(book);
            userBook.setStatus(status);
            userBookRepository.save(userBook);
        }
    }
    public void unmarkMovieForUser(Long userId, Long movieId) {
        Optional<UserMovie> userMovie = userMovieRepository.findByUserIdAndMovieId(userId, movieId);
        
        if (userMovie.isPresent()) {
            userMovieRepository.delete(userMovie.get());
        } else {
            throw new RuntimeException("No se encontró registro para desmarcar");
        }
    }
    public List<UserBook> getUserBooks(Long userId) {
        return userBookRepository.findByUserId(userId);
    }

    public List<UserBook> getUserBooksByStatus(Long userId, String status) {
        return userBookRepository.findByUserIdAndStatus(userId, status);
    }
    public void unmarkBookForUser(Long userId, Long bookId) {
        Optional<UserBook> userBook = userBookRepository.findByUserIdAndBookId(userId, bookId);
        
        if (userBook.isPresent()) {
            userBookRepository.delete(userBook.get());
        } else {
            throw new RuntimeException("No se encontró registro para desmarcar");
        }
}
}