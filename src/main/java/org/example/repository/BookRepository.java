package org.example.repository;

import org.apache.ibatis.annotations.Param;
import org.example.entity.Book;
import org.example.entity.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByIsCharge(boolean isCharge);

    Page<Book> findByBookType(BookType bookType, Pageable pageable);

    Book findByBookName(String bookName);

    List<Book> findByBookIdIn(List<Integer> bookIds);

    Boolean existsByBookId(Integer bookId);
    Page<Book> findByBookNameContainingOrAuthorContaining(String keyword, String keyword1, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.isCharge = :isCharge WHERE b.bookId = :bookId")
    void updateBookChargeStatus(@Param("bookId") int bookId, @Param("isCharge") int isCharge);



    //Page<Book> findAll(Pageable pageable);
}
