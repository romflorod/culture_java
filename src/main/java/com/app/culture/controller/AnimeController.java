package com.app.culture.controller;

import com.app.culture.model.Anime;
import com.app.culture.model.UserAnime;
import com.app.culture.service.AnimeService;
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
public class AnimeController {

    @Autowired
    private AnimeService animeService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/searchanime")
    public String searchAnimePage(HttpSession session, Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, cargamos sus animes marcados
        if (userId != null) {
            Map<Long, String> userAnimeStatuses = new HashMap<>();
            List<UserAnime> userAnimes = userService.getUserAnimes(userId);
            
            for (UserAnime userAnime : userAnimes) {
                userAnimeStatuses.put(userAnime.getAnime().getId(), userAnime.getStatus());
            }
            
            model.addAttribute("userAnimeStatuses", userAnimeStatuses);
        }
        
        return "searchanime";
    }

    @PostMapping("/searchanime")
    public String searchAnime(@RequestParam String query, HttpSession session, Model model) {
        List<Anime> animes = animeService.fetchAndSaveAnimes(query);
        
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, verificamos qué animes ya ha marcado
        if (userId != null) {
            Map<Long, String> userAnimeStatuses = new HashMap<>();
            List<UserAnime> userAnimes = userService.getUserAnimes(userId);
            
            for (UserAnime userAnime : userAnimes) {
                userAnimeStatuses.put(userAnime.getAnime().getId(), userAnime.getStatus());
            }
            
            model.addAttribute("userAnimeStatuses", userAnimeStatuses);
        }
        
        model.addAttribute("animes", animes);
        model.addAttribute("query", query);
        return "searchanime";
    }
    
    @PostMapping("/markAnime")
    public String markAnime(@RequestParam Long animeId,
                        @RequestParam String status,
                        @RequestParam(required = false) String query,
                        HttpSession session,
                        Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            userService.markAnimeForUser(userId, animeId, status);
            
            // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
            if (query != null && !query.isEmpty()) {
                // Ejecutar la misma búsqueda de nuevo para mantener resultados
                List<Anime> animes = animeService.fetchAndSaveAnimes(query);
                
                // Obtener estados actualizados de los animes del usuario
                Map<Long, String> userAnimeStatuses = new HashMap<>();
                List<UserAnime> userAnimes = userService.getUserAnimes(userId);
                
                for (UserAnime userAnime : userAnimes) {
                    userAnimeStatuses.put(userAnime.getAnime().getId(), userAnime.getStatus());
                }
                
                model.addAttribute("userAnimeStatuses", userAnimeStatuses);
                model.addAttribute("animes", animes);
                model.addAttribute("marked", true);
                model.addAttribute("query", query);
                return "searchanime";
            }
            
            return "redirect:/searchanime?marked=true";
        }
        
        return "redirect:/login";
    }

    @GetMapping("/myanimes")
    public String myAnimes(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            List<UserAnime> watched = userService.getUserAnimesByStatus(userId, "visto");
            List<UserAnime> following = userService.getUserAnimesByStatus(userId, "en_seguimiento");
            
            model.addAttribute("watched", watched);
            model.addAttribute("following", following);
            
            return "myanimes";
        }
        
        return "redirect:/login";
    }
    @PostMapping("/unmarkAnime")
    public String unmarkAnime(@RequestParam Long animeId,
                            @RequestParam(required = false) String query,
                            @RequestParam(required = false) String returnTo,
                            HttpSession session,
                            Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            try {
                userService.unmarkAnimeForUser(userId, animeId);
                
                // Si hay un parámetro de retorno, lo usamos para la redirección
                if (returnTo != null && returnTo.equals("myanimes")) {
                    return "redirect:/myanimes?unmarked=true";
                }
                
                // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
                if (query != null && !query.isEmpty()) {
                    // Ejecutar la misma búsqueda de nuevo para mantener resultados
                    List<Anime> animes = animeService.fetchAndSaveAnimes(query);
                    
                    // Obtener estados actualizados de los animes del usuario
                    Map<Long, String> userAnimeStatuses = new HashMap<>();
                    List<UserAnime> userAnimes = userService.getUserAnimes(userId);
                    
                    for (UserAnime userAnime : userAnimes) {
                        userAnimeStatuses.put(userAnime.getAnime().getId(), userAnime.getStatus());
                    }
                    
                    model.addAttribute("userAnimeStatuses", userAnimeStatuses);
                    model.addAttribute("animes", animes);
                    model.addAttribute("unmarked", true);
                    model.addAttribute("query", query);
                    return "searchanime";
                }
                
                return "redirect:/searchanime?unmarked=true";
            } catch (Exception e) {
                // Manejar error
                return "redirect:/searchanime?error=true";
            }
        }
        
        return "redirect:/login";
}
}