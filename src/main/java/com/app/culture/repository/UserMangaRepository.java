package com.app.culture.repository;

import com.app.culture.model.UserManga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMangaRepository extends JpaRepository<UserManga, Long> {
    List<UserManga> findByUserId(Long userId);
    List<UserManga> findByUserIdAndStatus(Long userId, String status);
    Optional<UserManga> findByUserIdAndMangaId(Long userId, Long mangaId);
}