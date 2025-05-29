package com.app.culture.controller;

import com.app.culture.model.Anime;
import com.app.culture.model.UserAnime;
import com.app.culture.service.AnimeService;
import com.app.culture.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    // Agregar este nuevo endpoint al controlador AnimeController
    @GetMapping("/animedetails/{id}")
    public String animeDetails(@PathVariable Long id, HttpSession session, Model model) {
        try {
            // Obtener detalles del anime
            Optional<Anime> animeOpt = animeService.getAnimeById(id);
            
            if (animeOpt.isPresent()) {
                Anime anime = animeOpt.get();
                model.addAttribute("anime", anime);
                
                // Verificar si el usuario ha marcado este anime
                Long userId = (Long) session.getAttribute("userId");
                if (userId != null) {
                    Optional<UserAnime> userAnimeOpt = userService.getUserAnimeByUserIdAndAnimeId(userId, id);
                    userAnimeOpt.ifPresent(userAnime -> {
                        model.addAttribute("userAnime", userAnime);
                        model.addAttribute("status", userAnime.getStatus());
                    });
                }
                
                return "animedetails";
            } else {
                System.err.println("Anime con ID " + id + " no encontrado");
                return "redirect:/searchanime?error=animenotfound";
            }
        } catch (Exception e) {
            System.err.println("Error al acceder a detalles del anime: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/searchanime?error=exception";
        }
    }

    @PostMapping("/generateAnimeRecommendations")
    public String generateAnimeRecommendations(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            List<UserAnime> userAnimes = userService.getUserAnimes(userId);

            String animeDataForGemini = userAnimes.stream().map(userAnime -> {
                String title = userAnime.getAnime().getTitle();
                String description = userAnime.getAnime().getDescription();
                String first10Words = "";
                if (description != null && !description.isEmpty()) {
                    String[] words = description.split("\\s+");
                    first10Words = String.join(" ", java.util.Arrays.copyOfRange(words, 0, Math.min(10, words.length)));
                }
                return "Título: " + title + ", Descripción: " + first10Words;
            }).collect(Collectors.joining("; "));

            String initialMessage = "Dame una recomendación de anime basada en estos que ya he visto: " + animeDataForGemini + ". Por favor, dame solo el título y una breve sinopsis.";

            String recommendation = "No se pudo obtener recomendación.";
            try {
                RestTemplate restTemplate = new RestTemplate();

                // Usar uno de los modelos disponibles según la respuesta del curl
                String modelName = "gemini-1.5-flash";  // Este modelo está disponible según tu respuesta de curl
                String apiKey = "";
                // Usar la versión v1 (no v1beta)
                String geminiApiUrl = "https://generativelanguage.googleapis.com/v1/models/" + modelName + ":generateContent?key=" + apiKey;
                
                Map<String, Object> requestBody = new HashMap<>();
                Map<String, Object> userContent = new HashMap<>();
                userContent.put("role", "user");
                Map<String, String> textPart = new HashMap<>();
                textPart.put("text", initialMessage);
                userContent.put("parts", Collections.singletonList(textPart));
                requestBody.put("contents", Collections.singletonList(userContent));

                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("temperature", 0.7);
                generationConfig.put("maxOutputTokens", 200);
                requestBody.put("generationConfig", generationConfig);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                // Agregar debug para ver qué se envía
                System.out.println("Enviando solicitud a Gemini: " + requestBody);
                
                ResponseEntity<Map> response = restTemplate.postForEntity(geminiApiUrl, requestEntity, Map.class);
                
                // Agregar debug para ver qué se recibe
                System.out.println("Respuesta de Gemini: " + response.getStatusCode());
                
                // El resto del código para procesar la respuesta sigue igual
                Map body = response.getBody();
                if (body != null && body.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map<String, Object> firstCandidate = candidates.get(0);
                        if (firstCandidate.containsKey("content")) {
                            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                            if (content.containsKey("parts")) {
                                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                                if (!parts.isEmpty()) {
                                    recommendation = (String) ((Map<String, Object>)parts.get(0)).get("text");
                                } else {
                                    recommendation = "Respuesta de Gemini vacía en 'parts'.";
                                }
                            } else {
                                recommendation = "Respuesta de Gemini sin 'parts'. Contenido: " + content.toString();
                            }
                        } else {
                            recommendation = "Respuesta de Gemini sin 'content'. Candidato: " + firstCandidate.toString();
                        }
                    } else {
                        recommendation = "Gemini no proporcionó candidatos para la recomendación.";
                    }
                } else {
                    recommendation = "Respuesta de Gemini malformada o vacía. Cuerpo: " + (body != null ? body.toString() : "null");
                }
            } catch (Exception e) {
                recommendation = "Error al obtener recomendación: " + e.getMessage();
                e.printStackTrace();
            }

            model.addAttribute("recommendation", recommendation);
            return "animerecommendation";
        }

        return "redirect:/login";
    }
    
}