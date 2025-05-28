package com.app.culture.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String googleBooksId;  // ID único de Google Books
    
    private String title;
    
    private String authors;  // Lista de autores como texto
    
    private String publisher;
    
    @Column(name = "published_date")
    private String publishedDate;
    
    @Column(length = 5000)
    private String description;
    
    private String imageUrl;  // URL de la portada
    
    private String pageCount;
    
    private String categories;  // Categorías/géneros separados por comas
    
    private String language;
    
    private String previewLink;  // Enlace a la vista previa en Google Books
}