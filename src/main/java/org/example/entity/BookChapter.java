package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "BookChapter")
public class BookChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chapterId;

    private int bookId;
    private int chapterNum;
    private String chapterName;
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(nullable = false, updatable = true)
    private LocalDateTime updateTime;
}
