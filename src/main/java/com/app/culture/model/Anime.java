package com.app.culture.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000) // Aumentar el tamaño máximo a 5000 caracteres
    private String description;

    private String imageUrl;

    private String trailerUrl;
}