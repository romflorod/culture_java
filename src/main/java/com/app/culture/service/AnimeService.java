package com.app.culture.service;

import com.app.culture.model.Anime;
import com.app.culture.repository.AnimeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnimeService {

    @Autowired
    private AnimeRepository animeRepository;

    private final String JIKAN_API_URL = "https://api.jikan.moe/v4/anime?q=";

   public List<Anime> fetchAndSaveAnimes(String query) {
    RestTemplate restTemplate = new RestTemplate();
    String url = JIKAN_API_URL + query;

    // Realizar la solicitud a la API
    JsonNode response = restTemplate.getForObject(url, JsonNode.class);

    List<Anime> animes = new ArrayList<>();
    if (response != null && response.has("data")) {
        for (JsonNode animeNode : response.get("data")) {
            String title = animeNode.get("title").asText();
            
            // Obtener la lista de animes con ese título
            List<Anime> existingAnimes = animeRepository.findByTitle(title);
            
            Anime anime;
            if (!existingAnimes.isEmpty()) {
                // Si existe al menos uno, usamos el primero
                anime = existingAnimes.get(0);
                
                // Opcionalmente, actualizar datos si es necesario
                if (animeNode.has("synopsis") && !anime.getDescription().equals(animeNode.get("synopsis").asText(""))) {
                    anime.setDescription(animeNode.has("synopsis") ? animeNode.get("synopsis").asText("") : "");
                }
            } else {
                // Si no existe, crear uno nuevo
                anime = new Anime();
                anime.setTitle(title);
                anime.setDescription(animeNode.has("synopsis") ? animeNode.get("synopsis").asText("") : "");
                anime.setImageUrl(animeNode.get("images").get("jpg").get("image_url").asText(""));
                anime.setTrailerUrl(animeNode.has("trailer") && !animeNode.get("trailer").isNull() ? 
                                  animeNode.get("trailer").get("url").asText("") : "");
            }

            // Guardar en la base de datos (ya sea nuevo o existente)
            animeRepository.save(anime);
            animes.add(anime);
        }
    }

    return animes;
}
    
    public Optional<Anime> getAnimeById(Long id) {
        return animeRepository.findById(id);
    }
    
    public List<Anime> getAllAnimes() {
        return animeRepository.findAll();
    }
}