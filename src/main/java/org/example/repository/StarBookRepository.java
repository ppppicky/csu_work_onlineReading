package org.example.repository;

import org.example.entity.StarBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarBookRepository extends JpaRepository<StarBook, Integer> {
    List<StarBook> findByUserId(Integer userId);

    Optional<StarBook> findByUserIdAndBookId(Integer userId, Integer bookId);

    boolean existsByUserIdAndBookId(Integer userId, Integer bookId);

    void delete(StarBook starBook);

    @Query("SELECT b, COUNT(s) FROM StarBook s JOIN Book b ON s.bookId = b.bookId " +
            "GROUP BY b ORDER BY COUNT(s) DESC")
    List<Object[]> findTopStarredBooks(Pageable pageable);

    Long countByBookId(Integer bookId);

    void deleteByBookId(Integer bookId);
}
