package org.example.repository;

import org.example.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    // 根据订单号查找订单
    Optional<Orders> findByOrderId(String orderId);

    // 自定义查询，支持用户ID必选，其他参数可选
    @Query("SELECT o FROM Orders o WHERE o.userId = :userId " +
            "AND (:name IS NULL OR o.name = :name) " +
            "AND (:state IS NULL OR o.state = :state) " +
            "AND (:bookId IS NULL OR o.bookId = :bookId) " +
            "AND (:startTime IS NULL OR o.createTime >= :startTime) " +
            "AND (:endTime IS NULL OR o.createTime <= :endTime)")
    List<Orders> findOrders(Integer userId, String name, String state, Integer bookId, LocalDateTime startTime, LocalDateTime endTime);
}
