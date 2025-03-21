package org.example.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BookType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookTypeId;

    @Column(nullable = false, length = 255)
    private String bookTypeName;

}
