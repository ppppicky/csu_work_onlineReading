package org.example.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "StarBook")
public class StarBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer starId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer bookId;

}
