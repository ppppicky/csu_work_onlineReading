package org.example.repository;

import org.example.dto.BookReadCountDTO;
import org.example.entity.Book;
import org.example.entity.ReadRecord;
import org.example.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@EnableJpaRepositories
@Repository
public interface ReadRepository extends JpaRepository<ReadRecord,Integer> {

    List<ReadRecord> findByBook(Book book);

    Optional<ReadRecord> findByUserAndBook(Users users, Book book);

    void deleteByBook(Book book);
    Optional<List<ReadRecord>> findByUser(Users users);

//    @Query("SELECT ReadRecord FROM ReadRecord r JOIN r.book b GROUP BY b.bookName ORDER BY COUNT(r) DESC")
//    List<BookReadCountDTO> getTopReadBooks();
//    Integer countActiveReaders(LocalDateTime localDateTime);
//    Long countByLastReadTimeAfter(LocalDateTime time);
//    ReadRecord findTopByOrderByLastReadTimeDesc();
}
