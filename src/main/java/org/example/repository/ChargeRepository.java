package org.example.repository;

import org.example.entity.Book;
import org.example.entity.ChargeManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ChargeRepository extends JpaRepository<ChargeManagement,Integer> {
    Optional<ChargeManagement> findByBook_BookId(int bookId);
    Optional<ChargeManagement> findByBook(Book book);

    @Query("SELECT SUM(cm.chargeMoney) FROM ChargeManagement cm")
    BigDecimal getTotalRevenue();



    // 根据 book 删除收费信息
    @Transactional
    void deleteByBook(Book book);
}
