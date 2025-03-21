package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity

public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "userName")
    private String userName;


    private String userPassword;

    private Byte userSex; // 1 male, 0 female
    private Byte isVip; // 1 VIP, 0 no
    private LocalDateTime vipTime;

    @Column(precision = 20, scale = 2)
    private BigDecimal userCredit;

    @Column(updatable = false)
    private LocalDateTime userRegTime;

    @Column(nullable = false)
    private Byte status = 1;  // 1 yes, 0 no

    @Version
    private Integer version;

}