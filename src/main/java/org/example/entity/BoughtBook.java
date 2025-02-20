package org.example.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "bought_book")
public class BoughtBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boughtId; // 购买记录ID

    @Column(nullable = false)
    private Integer userId; // 用户ID

    @Column(nullable = false)
    private Integer bookId; // 书籍ID
}
