package org.example.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class BoughtBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boughtId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer bookId;

    private LocalDateTime boughtTime;
}
