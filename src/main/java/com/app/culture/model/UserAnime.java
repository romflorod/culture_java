package com.app.culture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_animes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;
    
    @Column(nullable = false)
    private String status; // "visto" o "en_seguimiento"
    
    @Column(name = "marked_at")
    private LocalDateTime markedAt;
    
    @PrePersist
    protected void onCreate() {
        markedAt = LocalDateTime.now();
    }
}