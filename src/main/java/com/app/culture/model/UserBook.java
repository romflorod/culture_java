package com.app.culture.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_books")
@Data
public class UserBook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(nullable = false)
    private String status; // "leido" o "en_seguimiento" o "pendiente"
    
    private LocalDateTime markedAt;
    
    @PrePersist
    protected void onCreate() {
        markedAt = LocalDateTime.now();
    }
}