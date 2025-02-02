package org.example.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;

    @Column(nullable = false, length = 255)
    private String bookName;


    @Column(length = 255)
    private String author;

    @Column(length = 255)
    private String bookCover;

    private String bookDesc;

    private Integer bookPage;

    private Byte isCharge;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

   private LocalDateTime updateTime;

    private int bookTypeId;
    @Lob
    private byte[] epubFile;
}
