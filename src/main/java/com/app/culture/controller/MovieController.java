package com.app.culture.controller;

import com.app.culture.model.Movie;
import com.app.culture.model.UserMovie;
import com.app.culture.service.MovieService;
import com.app.culture.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/searchmovie")
    public String searchMoviePage(HttpSession session, Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, cargamos sus películas marcadas
        if (userId != null) {
            Map<Long, String> userMovieStatuses = new HashMap<>();
            List<UserMovie> userMovies = userService.getUserMovies(userId);
            
            for (UserMovie userMovie : userMovies) {
                userMovieStatuses.put(userMovie.getMovie().getId(), userMovie.getStatus());
            }
            
            model.addAttribute("userMovieStatuses", userMovieStatuses);
        }
        
        return "searchmovie";
    }
    
    @PostMapping("/searchmovie")
    public String searchMovie(@RequestParam String query, HttpSession session, Model model) {
        List<Movie> movies = movieService.searchAndSaveMovies(query);
        
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, verificamos qué películas ya ha marcado
        if (userId != null) {
            Map<Long, String> userMovieStatuses = new HashMap<>();
            List<UserMovie> userMovies = userService.getUserMovies(userId);
            
            for (UserMovie userMovie : userMovies) {
                userMovieStatuses.put(userMovie.getMovie().getId(), userMovie.getStatus());
            }
            
            model.addAttribute("userMovieStatuses", userMovieStatuses);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("query", query);
        return "searchmovie";
    }
    
    @PostMapping("/markMovie")
    public String markMovie(@RequestParam Long movieId,
                          @RequestParam String status,
                          @RequestParam(required = false) String query,
                          HttpSession session,
                          Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            userService.markMovieForUser(userId, movieId, status);
            
            // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
            if (query != null && !query.isEmpty()) {
                // Ejecutar la misma búsqueda de nuevo para mantener resultados
                List<Movie> movies = movieService.searchAndSaveMovies(query);
                
                // Obtener estados actualizados de las películas del usuario
                Map<Long, String> userMovieStatuses = new HashMap<>();
                List<UserMovie> userMovies = userService.getUserMovies(userId);
                
                for (UserMovie userMovie : userMovies) {
                    userMovieStatuses.put(userMovie.getMovie().getId(), userMovie.getStatus());
                }
                
                model.addAttribute("userMovieStatuses", userMovieStatuses);
                model.addAttribute("movies", movies);
                model.addAttribute("marked", true);
                model.addAttribute("query", query);
                return "searchmovie";
            }
            
            return "redirect:/searchmovie?marked=true";
        }
        
        return "redirect:/login";
    }
    
    @GetMapping("/mymovies")
    public String myMovies(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            List<UserMovie> watched = userService.getUserMoviesByStatus(userId, "visto");
            List<UserMovie> following = userService.getUserMoviesByStatus(userId, "en_seguimiento");
            
            model.addAttribute("watched", watched);
            model.addAttribute("following", following);
            
            return "mymovies";
        }
        
        return "redirect:/login";
    }
    @PostMapping("/unmarkMovie")
    public String unmarkMovie(@RequestParam Long movieId,
                            @RequestParam(required = false) String query,
                            @RequestParam(required = false) String returnTo,
                            HttpSession session,
                            Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            try {
                userService.unmarkMovieForUser(userId, movieId);
                
                // Si hay un parámetro de retorno, lo usamos para la redirección
                if (returnTo != null && returnTo.equals("mymovies")) {
                    return "redirect:/mymovies?unmarked=true";
                }
                
                // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
                if (query != null && !query.isEmpty()) {
                    // Ejecutar la misma búsqueda de nuevo para mantener resultados
                    List<Movie> movies = movieService.searchAndSaveMovies(query);
                    
                    // Obtener estados actualizados de las películas del usuario
                    Map<Long, String> userMovieStatuses = new HashMap<>();
                    List<UserMovie> userMovies = userService.getUserMovies(userId);
                    
                    for (UserMovie userMovie : userMovies) {
                        userMovieStatuses.put(userMovie.getMovie().getId(), userMovie.getStatus());
                    }
                    
                    model.addAttribute("userMovieStatuses", userMovieStatuses);
                    model.addAttribute("movies", movies);
                    model.addAttribute("unmarked", true);
                    model.addAttribute("query", query);
                    return "searchmovie";
                }
                
                return "redirect:/searchmovie?unmarked=true";
            } catch (Exception e) {
                // Manejar error
                return "redirect:/searchmovie?error=true";
            }
        }
        
        return "redirect:/login";
    }
}