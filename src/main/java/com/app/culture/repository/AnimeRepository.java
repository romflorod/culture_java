package com.app.culture.repository;

import com.app.culture.model.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    // Reemplazar el método que causa errores
    // Optional<Anime> findByTitle(String title);
    
    // Nuevo método que devuelve el primer anime con ese título
    @Query("SELECT a FROM Anime a WHERE a.title = :title ORDER BY a.id ASC")
    List<Anime> findByTitle(@Param("title") String title);
}