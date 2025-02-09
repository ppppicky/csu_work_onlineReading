package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChargeManagement")
@Data
public class ChargeManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cmId;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    private Integer freePage;

    @Column(precision = 20, scale = 2)
    private BigDecimal chargeMoney;

    private Byte isVIPFree;// 1:free
}
