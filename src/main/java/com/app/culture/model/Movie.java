package com.app.culture.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String imdbId;  // ID único de IMDB
    
    private String title;
    
    @Column(length = 5000)
    private String plot;    // Equivalente a la descripción
    
    @Column(name = "release_year") // Cambiamos el nombre de la columna en la BD
    private String year;
    
    private String director;
    
    private String actors;
    
    private String genre;
    
    private String imageUrl;  // Poster URL
    
    private String runtime;
    
    private String imdbRating;
}