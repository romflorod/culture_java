package com.app.culture.repository;

import com.app.culture.model.UserAnime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnimeRepository extends JpaRepository<UserAnime, Long> {
    List<UserAnime> findByUserId(Long userId);
    List<UserAnime> findByUserIdAndStatus(Long userId, String status);
    Optional<UserAnime> findByUserIdAndAnimeId(Long userId, Long animeId);
    boolean existsByUserIdAndAnimeId(Long userId, Long animeId);
}