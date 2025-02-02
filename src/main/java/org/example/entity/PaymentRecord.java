//package org.example.entity;
//
//
//import lombok.Data;
//
//import javax.persistence.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "PaymentRecord")
//@Data
//public class PaymentRecord {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer payRId;
//
//    @ManyToOne
//    @JoinColumn(name = "userId", nullable = false)
//    private users user;
//
//    @Column(nullable = false, length = 255)
//    private String payEvent;
//
//    @Column(precision = 20, scale = 2)
//    private BigDecimal payMoney;
//
//    @Column(nullable = false, updatable = false)
//    private LocalDateTime payTime;
//}
