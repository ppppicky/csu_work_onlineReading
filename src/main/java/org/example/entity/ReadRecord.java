//package org.example.entity;
//
//import lombok.Data;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "ReadRecord")
//@Data
//public class ReadRecord {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer readRId;
//
//    @ManyToOne
//    @JoinColumn(name = "userId", nullable = false)
//    private users user;
//
//    @ManyToOne
//    @JoinColumn(name = "bookId", nullable = false)
//    private Book book;
//
//    private LocalDateTime lastReadTime;
//
//    private Integer lastReadPage;
//}
