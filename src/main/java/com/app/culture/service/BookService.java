package com.app.culture.service;

import com.app.culture.model.Book;
import com.app.culture.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Value("${google.books.api.url}")
    private String apiUrl;
    
    @Value("${google.books.api.key}")
    private String apiKey;
    
    /**
     * Busca libros por título y los guarda en la base de datos
     */
    public List<Book> searchAndSaveBooks(String query) {
        List<Book> books = new ArrayList<>();
        
        try {
            // Construir la URL para la búsqueda
            String url = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("q", query)
                .queryParam("key", apiKey)
                .queryParam("maxResults", 20)
                .build()
                .toUriString();
                
            RestTemplate restTemplate = new RestTemplate();
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            
            // Verificar si hay resultados
            if (response != null && response.has("items")) {
                JsonNode results = response.get("items");
                
                // Recorrer cada resultado
                for (JsonNode result : results) {
                    String googleBooksId = result.get("id").asText();
                    
                    // Verificar si ya existe el libro en la base de datos
                    Optional<Book> existingBook = bookRepository.findByGoogleBooksId(googleBooksId);
                    
                    if (existingBook.isPresent()) {
                        // Si ya existe, añadirlo a la lista de resultados
                        books.add(existingBook.get());
                    } else {
                        // Si no existe, extraer detalles y guardar
                        JsonNode volumeInfo = result.get("volumeInfo");
                        
                        Book book = new Book();
                        book.setGoogleBooksId(googleBooksId);
                        book.setTitle(volumeInfo.has("title") ? volumeInfo.get("title").asText() : "");
                        book.setDescription(volumeInfo.has("description") ? volumeInfo.get("description").asText("") : "");
                        
                        // Extraer lista de autores y unirlos con coma
                        if (volumeInfo.has("authors") && volumeInfo.get("authors").isArray()) {
                            List<String> authors = new ArrayList<>();
                            for (JsonNode author : volumeInfo.get("authors")) {
                                authors.add(author.asText());
                            }
                            book.setAuthors(String.join(", ", authors));
                        }
                        
                        // Portada del libro
                        if (volumeInfo.has("imageLinks")) {
                            JsonNode imageLinks = volumeInfo.get("imageLinks");
                            book.setImageUrl(imageLinks.has("thumbnail") ? imageLinks.get("thumbnail").asText() : "");
                        }
                        
                        book.setPublisher(volumeInfo.has("publisher") ? volumeInfo.get("publisher").asText("") : "");
                        book.setPublishedDate(volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").asText("") : "");
                        book.setPageCount(volumeInfo.has("pageCount") ? volumeInfo.get("pageCount").asText("") : "");
                        book.setLanguage(volumeInfo.has("language") ? volumeInfo.get("language").asText("") : "");
                        book.setPreviewLink(volumeInfo.has("previewLink") ? volumeInfo.get("previewLink").asText("") : "");
                        
                        // Extraer categorías/géneros
                        if (volumeInfo.has("categories") && volumeInfo.get("categories").isArray()) {
                            List<String> categories = new ArrayList<>();
                            for (JsonNode category : volumeInfo.get("categories")) {
                                categories.add(category.asText());
                            }
                            book.setCategories(String.join(", ", categories));
                        }
                        
                        // Guardar el libro en la base de datos
                        bookRepository.save(book);
                        books.add(book);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return books;
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}