package org.example.repository;

import org.example.entity.Book;
import org.example.entity.ReadRecord;
import org.example.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadRepository extends JpaRepository<ReadRecord,Integer> {

    List<ReadRecord> findByBook(Book book);

    Optional<ReadRecord> findByUserAndBook(Users users, Book book);

    List<ReadRecord> findByUser(Users users);
}
