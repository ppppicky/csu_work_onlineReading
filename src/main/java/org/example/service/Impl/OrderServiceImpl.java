package org.example.service.Impl;

import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AliPayConfig;
import org.example.dto.OrderQueryDTO;
import org.example.entity.BoughtBook;
import org.example.entity.Orders;
import org.example.entity.Users;
import org.example.repository.BoughtBookRepository;
import org.example.repository.OrdersRepository;
import org.example.repository.UserRepository;
import org.example.service.OrderService;
import org.example.util.AliPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final BoughtBookRepository boughtBookRepository;

    @Resource
    private AliPayUtil aliPayUtil; // 负责调用支付宝关闭订单的工具类

    @Resource
    private AliPayConfig aliPayConfig; // 读取支付宝配置

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

        /**
         * 生成唯一订单号
         * 格式: yyyyMMdd + 时间戳后5位 + 随机3位
         * 例子: 202402191661165525060
         *
         * @return 订单号字符串
         */
        // 1. 获取当前日期 yyyyMMdd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePrefix = dateFormat.format(new Date());

        // 2. 获取当前时间戳（毫秒级）并取后5位
        String timestampSuffix = String.valueOf(System.currentTimeMillis()).substring(8, 13);

//        // 3. 生成 3 位随机数
//        Random random = new Random();
//        int randomSuffix = 100 + random.nextInt(900); // 生成100-999之间的随机数

        //使用uuid生成后八位
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // 4. 拼接订单号
        String orderId = datePrefix + timestampSuffix + uuid;

        // 创建订单
        orderRequest.setCreateTime(LocalDateTime.now());
        orderRequest.setState("PENDING");
        orderRequest.setOrderId(orderId);
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
            boughtBook.setBoughtTime(LocalDateTime.now());
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



    /**
     * 定时关闭超时订单（每 10 分钟执行一次）
     */
    @Scheduled(fixedRate = 600000) // 10 分钟执行一次
    public void autoCancelOrders() {
        log.info("========= 开始检查超时未支付订单 =========");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.minusMinutes(30); // 设定 30 分钟订单超时

        // 查询所有超时未支付的订单
        List<Orders> expiredOrders = ordersRepository.findByStateAndCreateTimeBefore("PENDING", expiryTime);

        for (Orders order : expiredOrders) {
            try {
                // 调用支付宝 API 关闭订单
                boolean isClosed = aliPayUtil.closeOrder(order.getOrderId(), aliPayConfig);

                // 更新数据库订单状态
                order.setState("CANCELLED");
                ordersRepository.save(order);
                log.info("订单 {} 已自动关闭", order.getOrderId());

            } catch (AlipayApiException e) {
                log.error("关闭支付宝订单失败: {}", order.getOrderId(), e);
            }
        }

        log.info("========= 超时未支付订单检查完成 =========");
    }
}
