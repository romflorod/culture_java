package com.app.culture.service;

import com.app.culture.model.Manga;
import com.app.culture.repository.MangaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MangaService {

    @Autowired
    private MangaRepository mangaRepository;

    private final String JIKAN_API_URL = "https://api.jikan.moe/v4/manga?q=";

    public List<Manga> fetchAndSaveMangas(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String url = JIKAN_API_URL + query;

        // Realizar la solicitud a la API
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        List<Manga> mangas = new ArrayList<>();
        if (response != null && response.has("data")) {
            for (JsonNode mangaNode : response.get("data")) {
                String title = mangaNode.get("title").asText();
                
                // Obtener la lista de mangas con ese título
                List<Manga> existingMangas = mangaRepository.findByTitle(title);
                
                Manga manga;
                if (!existingMangas.isEmpty()) {
                    // Si existe al menos uno, usamos el primero
                    manga = existingMangas.get(0);
                    
                    // Opcionalmente, actualizar datos si es necesario
                    if (mangaNode.has("synopsis") && !manga.getDescription().equals(mangaNode.get("synopsis").asText(""))) {
                        manga.setDescription(mangaNode.has("synopsis") ? mangaNode.get("synopsis").asText("") : "");
                    }
                } else {
                    // Si no existe, crear uno nuevo
                    manga = new Manga();
                    manga.setTitle(title);
                    manga.setDescription(mangaNode.has("synopsis") ? mangaNode.get("synopsis").asText("") : "");
                    manga.setImageUrl(mangaNode.get("images").get("jpg").get("image_url").asText(""));
                    
                    // Información específica de manga
                    manga.setChapters(mangaNode.has("chapters") && !mangaNode.get("chapters").isNull() ? 
                                      mangaNode.get("chapters").asText("?") : "?");
                    manga.setVolumes(mangaNode.has("volumes") && !mangaNode.get("volumes").isNull() ? 
                                    mangaNode.get("volumes").asText("?") : "?");
                    manga.setStatus(mangaNode.has("status") ? mangaNode.get("status").asText("") : "");
                    
                    // URL para detalles en MyAnimeList
                    manga.setDetailsUrl(mangaNode.has("url") ? mangaNode.get("url").asText("") : "");
                }

                // Guardar en la base de datos (ya sea nuevo o existente)
                mangaRepository.save(manga);
                mangas.add(manga);
            }
        }

        return mangas;
    }
    
    public Optional<Manga> getMangaById(Long id) {
        return mangaRepository.findById(id);
    }
    
    public List<Manga> getAllMangas() {
        return mangaRepository.findAll();
    }
}