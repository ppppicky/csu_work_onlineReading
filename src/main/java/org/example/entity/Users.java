package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    // @Column(nullable = false, length = 50)
    @Column(name = "userName")
    private String userName;

    //@Column(nullable = false, length = 50)
    private String userPassword;

    private Byte userSex; // 1 male, 0 female
    private Byte isVip; // 1 VIP, 0 non
    private LocalDateTime vipTime;

    @Column(precision = 20, scale = 2)
    private BigDecimal userCredit;

    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userRegTime;

    @Column(nullable = false)
    private Byte status = 1;  // 1 yes, 0 no

}