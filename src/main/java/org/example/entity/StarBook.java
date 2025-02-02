//package org.example.entity;
//
//import lombok.Data;
//
//import javax.persistence.*;
//
//@Entity
//@Data
//@Table(name = "StarBook")
//public class StarBook {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer starBId;
//
//    @ManyToOne
//    @JoinColumn(name = "userId", nullable = false)
//    private users user;
//
//    @ManyToOne
//    @JoinColumn(name = "bookId", nullable = false)
//    private Book book;
//
//}
