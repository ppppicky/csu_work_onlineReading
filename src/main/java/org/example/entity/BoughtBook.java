//package org.example.entity;
//
//import lombok.Data;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "BoughtBook")
//@Data
//public class BoughtBook {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer boughtBId;
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
