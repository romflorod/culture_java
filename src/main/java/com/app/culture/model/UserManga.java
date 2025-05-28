package com.app.culture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_mangas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManga {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "manga_id", nullable = false)
    private Manga manga;
    
    @Column(nullable = false)
    private String status; // "leido", "en_seguimiento" o "pendiente"
    
    @Column(name = "marked_at")
    private LocalDateTime markedAt;
    
    @PrePersist
    protected void onCreate() {
        markedAt = LocalDateTime.now();
    }
}