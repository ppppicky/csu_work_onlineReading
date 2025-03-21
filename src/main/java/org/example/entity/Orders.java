package org.example.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 订单ID

    @ApiModelProperty("订单名称 (\"recharge\"充值;\"vip_year\"年会员;\"vip_month\"月会员;\"vip_season\"季会员;\"bought_book\"购书)")
    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    private String orderId; // 系统生成的订单号

    @Column(length = 64)
    private String alipayNo; // 支付宝交易号 (如果是余额支付，此字段为空)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now(); // 订单创建时间

    private LocalDateTime payTime; // 支付时间

    @Column(nullable = false, length = 10)
    private String state = "PENDING"; // 订单状态 (PENDING, PAID, CANCELLED)

    private Integer bookId; // 书籍ID（可为空，仅购书订单使用）

    @Column(nullable = false)
    private Integer userId; // 发起支付的用户ID

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total; // 订单金额

    @Column(length = 10)
    private String paymentMethod; // 支付方式 (ALIPAY=支付宝, BALANCE=余额)
}
