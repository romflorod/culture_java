package com.app.culture.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Manga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;

    private String imageUrl;
    
    private String chapters;
    
    private String volumes;
    
    private String status;
    
    // URL para visualizar detalles del manga en MyAnimeList
    private String detailsUrl;
}