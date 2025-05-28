package com.app.culture.repository;

import com.app.culture.model.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findByUserId(Long userId);
    List<UserBook> findByUserIdAndStatus(Long userId, String status);
    Optional<UserBook> findByUserIdAndBookId(Long userId, Long bookId);
}