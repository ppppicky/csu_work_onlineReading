package org.example.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
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

    private String bookDesc;//description

    private Integer bookPage;//chapter_count

    private Byte isCharge;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ManyToOne
    @JoinColumn(name = "bookTypeId", nullable = false)
    private BookType bookType;



}
