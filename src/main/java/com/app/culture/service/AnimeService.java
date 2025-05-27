package com.app.culture.service;

import com.app.culture.model.Anime;
import com.app.culture.repository.AnimeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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
                Anime anime = new Anime();
                anime.setTitle(animeNode.get("title").asText());
                anime.setDescription(animeNode.has("synopsis") ? animeNode.get("synopsis").asText("") : ""); // Manejar descripciones nulas
                anime.setImageUrl(animeNode.get("images").get("jpg").get("image_url").asText(""));
                anime.setTrailerUrl(animeNode.get("trailer").get("url").asText(""));

                // Guardar en la base de datos
                animeRepository.save(anime);
                animes.add(anime);
            }
        }

        return animes;
    }
}