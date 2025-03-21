package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
public class ChargeManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cmId;

    @ManyToOne
    @JoinColumn(name = "bookId")
    private Book book;

    private Integer freeChapter;

    @Column(precision = 20, scale = 2)
    private BigDecimal chargeMoney;

    private Byte isVipFree;// 1:free
}
