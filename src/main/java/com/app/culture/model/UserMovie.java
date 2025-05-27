package com.app.culture.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_movies")
@Data
public class UserMovie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @Column(nullable = false)
    private String status; // "visto" o "en_seguimiento"
    
    private LocalDateTime markedAt;
    
    @PrePersist
    protected void onCreate() {
        markedAt = LocalDateTime.now();
    }
}