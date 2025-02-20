package org.example.repository;

import org.example.entity.BoughtBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoughtBookRepository extends JpaRepository<BoughtBook, Integer> {
    List<BoughtBook> findByUserId(Integer userId);
    boolean existsByUserIdAndBookId(Integer userId, Integer bookId);

    // 判断用户是否已购买指定书籍
    Optional<BoughtBook> findByUserIdAndBookId(Integer userId, Integer bookId);
}
