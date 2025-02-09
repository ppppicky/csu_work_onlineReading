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
//    private Integer readId;
//
//    @ManyToOne
//   // @JoinColumn(name = "userId", nullable = false)
//    private  Integer userId;
//
//    @ManyToOne
//   // @JoinColumn(name = "bookId", nullable = false)
//    private Integer bookId;
//
//    private LocalDateTime lastReadTime;
//
//    private Integer lastReadPage;
//}
