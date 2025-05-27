package com.app.culture.repository;

import com.app.culture.model.UserMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {
    List<UserMovie> findByUserId(Long userId);
    List<UserMovie> findByUserIdAndStatus(Long userId, String status);
    Optional<UserMovie> findByUserIdAndMovieId(Long userId, Long movieId);
}