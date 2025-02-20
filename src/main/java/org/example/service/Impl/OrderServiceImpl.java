package org.example.service.Impl;

import org.example.dto.OrderQueryDTO;
import org.example.entity.BoughtBook;
import org.example.entity.Orders;
import org.example.entity.Users;
import org.example.repository.BoughtBookRepository;
import org.example.repository.OrdersRepository;
import org.example.repository.UserRepository;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final BoughtBookRepository boughtBookRepository;

    @Autowired
    public OrderServiceImpl(OrdersRepository ordersRepository, UserRepository userRepository, BoughtBookRepository boughtBookRepository) {
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
        this.boughtBookRepository = boughtBookRepository;
    }

    @Override
    public List<Orders> queryOrders(OrderQueryDTO dto) {
        return ordersRepository.findOrders(dto.getUserId(), dto.getName(), dto.getState(),
                dto.getBookId(), dto.getStartTime(), dto.getEndTime());
    }

    @Override
    public ResponseEntity<String> createOrder(Orders orderRequest) {
        // 验证用户
        Optional<Users> optionalUser = userRepository.findById(orderRequest.getUserId());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户不存在");
        }

        Users user = optionalUser.get();

        // 校验订单类型
        List<String> validNames = Arrays.asList("recharge", "vip_year", "vip_month", "vip_season", "bought_book");
        if (!validNames.contains(orderRequest.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("订单类型无效：" + orderRequest.getName());
        }

        // 购书订单需传入 book_id
        if ("bought_book".equals(orderRequest.getName()) && orderRequest.getBookId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("购书订单必须包含 book_id");
        }

        // 确保订单号唯一
        if (ordersRepository.findByOrderId(orderRequest.getOrderId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("订单号已存在，请更换 order_id");
        }

        // 创建订单
        orderRequest.setCreateTime(LocalDateTime.now());
        orderRequest.setState("PENDING");
        ordersRepository.save(orderRequest);
        return ResponseEntity.ok("订单创建成功，订单号：" + orderRequest.getOrderId());
    }

    @Override
    public ResponseEntity<String> payWithBalance(String orderId) {
        Optional<Orders> optionalOrder = ordersRepository.findByOrderId(orderId);
        if (!optionalOrder.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("订单不存在");
        }

        Orders order = optionalOrder.get();
        if (!"PENDING".equals(order.getState())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("订单已支付或已取消");
        }

        Optional<Users> optionalUser = userRepository.findById(order.getUserId());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }

        Users user = optionalUser.get();
        BigDecimal userBalance = user.getUserCredit();
        BigDecimal orderAmount = order.getTotal();

        // 余额不足
        if (userBalance.compareTo(orderAmount) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("余额不足，请充值。当前余额：" + userBalance + "，订单金额：" + orderAmount);
        }

        // 扣除余额
        user.setUserCredit(userBalance.subtract(orderAmount));

        // 处理不同的订单类型
        if (order.getName().equals("vip_year") || order.getName().equals("vip_month") || order.getName().equals("vip_season")) {
            LocalDateTime now = LocalDateTime.now();
            if (user.getVipTime() == null || user.getVipTime().isBefore(now)) {
                user.setVipTime(now);
            }
            switch (order.getName()) {
                case "vip_year":
                    user.setVipTime(user.getVipTime().plusYears(1));
                    break;
                case "vip_season":
                    user.setVipTime(user.getVipTime().plusMonths(3));
                    break;
                case "vip_month":
                    user.setVipTime(user.getVipTime().plusMonths(1));
                    break;
            }
            user.setIsVip((byte) 1);
        } else if (order.getName().equals("bought_book")) {
            if (order.getBookId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("购书订单必须包含 book_id");
            }

            if (boughtBookRepository.existsByUserIdAndBookId(user.getUserId(), order.getBookId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户已购买该书，无需重复购买");
            }

            BoughtBook boughtBook = new BoughtBook();
            boughtBook.setUserId(user.getUserId());
            boughtBook.setBookId(order.getBookId());
            boughtBookRepository.save(boughtBook);
        }

        // 更新订单状态
        order.setState("PAID");
        order.setPayTime(LocalDateTime.now());
        order.setPaymentMethod("BALANCE");

        // 保存更改
        userRepository.save(user);
        ordersRepository.save(order);

        return ResponseEntity.ok("支付成功！订单号：" + orderId + "，支付方式：余额支付");
    }

    @Override
    public ResponseEntity<String> cancelOrder(String orderId) {
        Optional<Orders> optionalOrder = ordersRepository.findByOrderId(orderId);
        if (!optionalOrder.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("订单不存在");
        }

        Orders order = optionalOrder.get();

        // **订单状态为 PAID（已支付），不能取消**
        if ("PAID".equals(order.getState())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("订单已支付，无法取消");
        }

        // **订单状态已是 CANCELLED（已取消），不能重复取消**
        if ("CANCELLED".equals(order.getState())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("订单已取消，无法重复取消");
        }

        // **订单状态为 PENDING（待支付），可以取消**
        if ("PENDING".equals(order.getState())) {
            order.setState("CANCELLED");
            ordersRepository.save(order);
            return ResponseEntity.ok("订单已成功取消，订单号：" + orderId);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("订单状态不允许取消");
    }
}
