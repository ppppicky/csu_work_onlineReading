package org.example.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
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

    private Integer bookPage;//chapter_cnt

    private Byte isCharge;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @ManyToOne
    @JoinColumn(name = "bookTypeId", nullable = false)
    private BookType bookType;

    @Lob
    private byte[] epubFile;

}
