package org.example.repository;

import org.example.entity.Book;
import org.example.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByIsCharge(boolean IsCharge);

    List<Book> findByBookType(BookType BookType);

    Book findByBookName(String BookName);

    List<Book> findByBookIdIn(List<Integer> bookIds);

    Boolean existsByBookId(Integer bookId);

    //Page<Book> findAll(Pageable pageable);
}
