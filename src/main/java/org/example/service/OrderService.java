package org.example.service;

import org.example.dto.OrderQueryDTO;
import org.example.entity.Orders;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    List<Orders> queryOrders(OrderQueryDTO dto);
    ResponseEntity<String> createOrder(Orders orderRequest);
    ResponseEntity<String> payWithBalance(String orderId);
    ResponseEntity<String> cancelOrder(String orderId);
}
