package com.app.culture.service;

import com.app.culture.model.Movie;
import com.app.culture.repository.MovieRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Value("${omdb.api.key}")
    private String apiKey;
    
    @Value("${omdb.api.url}")
    private String apiUrl;
    
    /**
     * Busca películas por título y las guarda en la base de datos
     */
    public List<Movie> searchAndSaveMovies(String query) {
    List<Movie> movies = new ArrayList<>();
    
    try {
        // Construir la URL para la búsqueda
        String url = UriComponentsBuilder
            .fromHttpUrl(apiUrl)
            .queryParam("apikey", apiKey)
            .queryParam("s", query)  // Búsqueda por título
            .build()
            .toUriString();
            
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        
        // Verificar si hay resultados
        if (response != null && response.has("Response") && response.get("Response").asText().equals("True")) {
            JsonNode results = response.get("Search");
            
            // Recorrer cada resultado
            for (JsonNode result : results) {
                String imdbId = result.get("imdbID").asText(); // Corregido: "imdbID" en vez de "imdbId"
                
                // Verificar si ya existe la película en la base de datos
                Optional<Movie> existingMovie = movieRepository.findByImdbId(imdbId);
                
                if (existingMovie.isPresent()) {
                    // Si ya existe, añadirla a la lista de resultados
                    movies.add(existingMovie.get());
                } else {
                    // Si no existe, obtener detalles y guardar
                    Movie movie = getAndSaveMovieDetails(imdbId);
                    if (movie != null) {
                        movies.add(movie);
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return movies;
}
    /**
     * Obtiene los detalles de una película por su ID de IMDB
     */
    private Movie getAndSaveMovieDetails(String imdbId) {
        try {
            // Construir la URL para obtener los detalles
            String url = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("i", imdbId)  // Búsqueda por IMDB ID
                .queryParam("plot", "full")  // Obtener la trama completa
                .build()
                .toUriString();
                
            RestTemplate restTemplate = new RestTemplate();
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            
            // Verificar si hay resultados válidos
            if (response != null && response.has("Response") && response.get("Response").asText().equals("True")) {
                Movie movie = new Movie();
                movie.setImdbId(imdbId);
                movie.setTitle(response.get("Title").asText(""));
                movie.setPlot(response.get("Plot").asText(""));
                movie.setYear(response.get("Year").asText(""));
                movie.setDirector(response.get("Director").asText(""));
                movie.setActors(response.get("Actors").asText(""));
                movie.setGenre(response.get("Genre").asText(""));
                movie.setImageUrl(response.get("Poster").asText(""));
                movie.setRuntime(response.get("Runtime").asText(""));
                movie.setImdbRating(response.get("imdbRating").asText(""));
                
                // Guardar y retornar la película
                return movieRepository.save(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }
    
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
}