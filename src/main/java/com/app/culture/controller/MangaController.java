package com.app.culture.controller;

import com.app.culture.model.Manga;
import com.app.culture.model.UserManga;
import com.app.culture.service.MangaService;
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
public class MangaController {

    @Autowired
    private MangaService mangaService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/searchmanga")
    public String searchMangaPage(HttpSession session, Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, cargamos sus mangas marcados
        if (userId != null) {
            Map<Long, String> userMangaStatuses = new HashMap<>();
            List<UserManga> userMangas = userService.getUserMangas(userId);
            
            for (UserManga userManga : userMangas) {
                userMangaStatuses.put(userManga.getManga().getId(), userManga.getStatus());
            }
            
            model.addAttribute("userMangaStatuses", userMangaStatuses);
        }
        
        return "searchmanga";
    }

    @PostMapping("/searchmanga")
    public String searchManga(@RequestParam String query, HttpSession session, Model model) {
        List<Manga> mangas = mangaService.fetchAndSaveMangas(query);
        
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, verificamos qué mangas ya ha marcado
        if (userId != null) {
            Map<Long, String> userMangaStatuses = new HashMap<>();
            List<UserManga> userMangas = userService.getUserMangas(userId);
            
            for (UserManga userManga : userMangas) {
                userMangaStatuses.put(userManga.getManga().getId(), userManga.getStatus());
            }
            
            model.addAttribute("userMangaStatuses", userMangaStatuses);
        }
        
        model.addAttribute("mangas", mangas);
        model.addAttribute("query", query);
        return "searchmanga";
    }
    
    @PostMapping("/markManga")
    public String markManga(@RequestParam Long mangaId,
                        @RequestParam String status,
                        @RequestParam(required = false) String query,
                        HttpSession session,
                        Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            userService.markMangaForUser(userId, mangaId, status);
            
            // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
            if (query != null && !query.isEmpty()) {
                // Ejecutar la misma búsqueda de nuevo para mantener resultados
                List<Manga> mangas = mangaService.fetchAndSaveMangas(query);
                
                // Obtener estados actualizados de los mangas del usuario
                Map<Long, String> userMangaStatuses = new HashMap<>();
                List<UserManga> userMangas = userService.getUserMangas(userId);
                
                for (UserManga userManga : userMangas) {
                    userMangaStatuses.put(userManga.getManga().getId(), userManga.getStatus());
                }
                
                model.addAttribute("userMangaStatuses", userMangaStatuses);
                model.addAttribute("mangas", mangas);
                model.addAttribute("marked", true);
                model.addAttribute("query", query);
                return "searchmanga";
            }
            
            return "redirect:/searchmanga?marked=true";
        }
        
        return "redirect:/login";
    }

    @GetMapping("/mymangas")
    public String myMangas(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            List<UserManga> read = userService.getUserMangasByStatus(userId, "leido");
            List<UserManga> following = userService.getUserMangasByStatus(userId, "en_seguimiento");
            List<UserManga> pending = userService.getUserMangasByStatus(userId, "pendiente");
            
            model.addAttribute("read", read);
            model.addAttribute("following", following);
            model.addAttribute("pending", pending);
            
            return "mymangas";
        }
        
        return "redirect:/login";
    }
    
    @PostMapping("/unmarkManga")
    public String unmarkManga(@RequestParam Long mangaId,
                           @RequestParam(required = false) String query,
                           @RequestParam(required = false) String returnTo,
                           HttpSession session,
                           Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            try {
                userService.unmarkMangaForUser(userId, mangaId);
                
                // Si hay un parámetro de retorno, lo usamos para la redirección
                if (returnTo != null && returnTo.equals("mymangas")) {
                    return "redirect:/mymangas?unmarked=true";
                }
                
                // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
                if (query != null && !query.isEmpty()) {
                    // Ejecutar la misma búsqueda de nuevo para mantener resultados
                    List<Manga> mangas = mangaService.fetchAndSaveMangas(query);
                    
                    // Obtener estados actualizados de los mangas del usuario
                    Map<Long, String> userMangaStatuses = new HashMap<>();
                    List<UserManga> userMangas = userService.getUserMangas(userId);
                    
                    for (UserManga userManga : userMangas) {
                        userMangaStatuses.put(userManga.getManga().getId(), userManga.getStatus());
                    }
                    
                    model.addAttribute("userMangaStatuses", userMangaStatuses);
                    model.addAttribute("mangas", mangas);
                    model.addAttribute("unmarked", true);
                    model.addAttribute("query", query);
                    return "searchmanga";
                }
                
                return "redirect:/searchmanga?unmarked=true";
            } catch (Exception e) {
                // Manejar error
                return "redirect:/searchmanga?error=true";
            }
        }
        
        return "redirect:/login";
    }
}