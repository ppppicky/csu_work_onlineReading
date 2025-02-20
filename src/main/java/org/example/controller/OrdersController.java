package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderQueryDTO;
import org.example.entity.Orders;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@Api(tags = "订单管理")
public class OrdersController {

    private final OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 统一订单查询接口
     */
    @ApiOperation(value = "统一订单查询接口")
    @PostMapping("/query")
    public ResponseEntity<List<Orders>> queryOrders(@RequestBody OrderQueryDTO dto) {
        if (dto.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Orders> orders = orderService.queryOrders(dto);
        return orders.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(orders);
    }

    /**
     * 创建订单
     */
    @PostMapping("/createOrder")
    @ApiOperation(value = "创建订单")
    public ResponseEntity<String> createOrder(@RequestBody Orders orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    /**
     * 余额支付订单
     */
    @PostMapping("/payWithBalance")
    @ApiOperation(value = "使用余额支付订单")
    public ResponseEntity<String> payWithBalance(@RequestParam String orderId) {
        return orderService.payWithBalance(orderId);
    }

    /**
     * 取消订单接口
     * @param orderId 订单号
     * @return 取消订单结果
     */
    @PostMapping("/cancelOrder")
    @ApiOperation(value = "取消订单")
    public ResponseEntity<String> cancelOrder(@RequestParam String orderId) {
        return orderService.cancelOrder(orderId);
    }
}
