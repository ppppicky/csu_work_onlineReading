package org.example.repository;

import org.example.entity.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTypeRepository extends JpaRepository<BookType,Integer> {
    BookType findByBookTypeName(String BookTypeName);
    Page<BookType> findAll(Pageable pageable);
}
