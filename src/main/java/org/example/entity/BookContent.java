//package org.example.entity;
//
//import lombok.Data;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//@Table(name = "BookContent")
//public class BookContent {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int contentId;
//    private int chapterId;
//    private String content;
//
//    @Column(nullable = false, updatable = false)
//    private LocalDateTime createTime;
//
//    @Column(nullable = false, updatable = true)
//    private LocalDateTime updateTime;
//}
//
